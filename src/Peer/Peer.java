package Peer;

import Discovery.RendezvousMessages.*;
import Discovery.RendezvousServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Peer {
    // If currentRendezvous == null then we haven't joined a server
    private InetSocketAddress currentRendezvous;

    // The name the Peer is using to represent itself.
    private String peerName;

    // The Peer's thread that is listening for other Peer connections.
    private PeerListenerThread peerListenerThread;

    // The Peers this Peer is currently talking to.
    private Map<String, PeerHandlerThread> connectedPeers = new HashMap<>();

    // The cached version of the peerMap from the Rendezvous Server
    private Map<String, InetSocketAddress> peerMap;

    public Peer() {
        // Peer starts listening for other peers on a random port.
        this.peerListenerThread = new PeerListenerThread(this);
        Thread thread = new Thread(this.peerListenerThread);
        thread.start();
    }

    /**
     * Makes the Peer join the given Rendezvous Server for Peer discovery at the default port.
     *
     * @param rendezvousAddress The Rendezvous Server's address.
     * @param peerName The name to use on the Rendezvous Server.
     */
    public void joinRendezvous(InetAddress rendezvousAddress, String peerName) throws IOException {
        this.joinRendezvous(rendezvousAddress, RendezvousServer.DEFAULT_PORT, peerName);
    }

    /**
     * Makes the Peer join the given Rendezvous Server for Peer discovery.
     *
     * @param rendezvousAddress The Rendezvous Server's address.
     * @param rendezvousPort The port to connect to on the Rendezvous Server
     * @param peerName The name to use on the Rendezvous Server.
     */
    public void joinRendezvous(InetAddress rendezvousAddress, int rendezvousPort, String peerName) throws IOException {
        this.leaveCurrentRendezvous();
        InetSocketAddress newRendezvousServer = new InetSocketAddress(rendezvousAddress, rendezvousPort);
        JoinResponse response = (JoinResponse) sendMessageToRendezvous(newRendezvousServer, new JoinRequest(peerName, this.getListeningPort()));

        if (!response.isSuccessful) {
            throw new IOException("Rendezvous Join unsuccessful.");
        }

        // Update state after message is successful.
        this.currentRendezvous = newRendezvousServer;
        this.peerName = peerName;
        this.peerMap = null;
    }

    /**
     * Makes the Peer leave their current Rendezvous Server.
     */
    public void leaveCurrentRendezvous() throws IOException {
        if (this.currentRendezvous == null) {
            // No work needed to be done.
            return;
        }

        LeaveResponse response = (LeaveResponse) sendMessageToRendezvous(this.currentRendezvous, new LeaveRequest(this.getListeningPort()));

        if (!response.isSuccessful) {
            throw new IOException("Rendezvous Leave unsuccessful.");
        }

        this.currentRendezvous = null;
        this.peerName = null;
        this.peerMap = null;
    }

    /**
     * Refreshes and returns the current Peer Map.
     *
     * @return The current map of peers.
     */
    public Map<String, InetSocketAddress> refreshAndGetPeerMap() throws IOException {
        if (this.currentRendezvous == null) {
            // Not connected to a server
            throw new IllegalStateException("Must join a Rendezvous Server first.");
        }

        GetListResponse response = (GetListResponse) sendMessageToRendezvous(this.currentRendezvous, new GetListRequest());
        this.peerMap = response.peerMap;

        return this.peerMap;
    }

    /**
     * Returns the Peer Map that we most recently obtained from the Rendezvous Server.
     *
     * @return The cached Peer Map. May be outdated.
     */
    public Map<String, InetSocketAddress> getCachedPeerMap() {
        return this.peerMap;
    }

    /**
     * Connects to the given Peer. Peer name must be recognized from Rendezvous Server.
     *
     * @param peerName The name of the peer to connect to.
     */
    public void connectToPeer(String peerName) throws IOException {
        if (!this.peerMap.containsKey(peerName)) {
            throw new IllegalArgumentException("Peer Name not recognized.");
        }

        if (this.connectedPeers.containsKey(peerName)) {
            throw new IllegalArgumentException("Peer is already connected.");
        }

        InetSocketAddress peerAddress = this.peerMap.get(peerName);

        Socket peerSocket = new Socket();
        peerSocket.connect(peerAddress);

        // Peer is expecting first message to be our name
        ObjectOutputStream toPeer = new ObjectOutputStream(peerSocket.getOutputStream());
        toPeer.writeObject(new PeerMessage(PeerMessage.MessageType.NAME, this.peerName));

        // New thread will continue communicating with this Peer. It will eventually close the socket.
        PeerHandlerThread peerHandlerThread = new PeerHandlerThread(this, peerSocket);
        Thread thread = new Thread(peerHandlerThread);
        thread.start();

        this.addConnection(peerName, peerHandlerThread);
    }

    /**
     * Disconnects from a connected Peer.
     *
     * @param peerName The peer to disconnect from.
     */
    public void disconnectFromPeer(String peerName) {
        if (!this.connectedPeers.containsKey(peerName)) {
            throw new IllegalArgumentException("Peer Name not recognized");
        }

        PeerHandlerThread peerHandlerThread = this.connectedPeers.get(peerName);
        peerHandlerThread.closeConnection();
        this.removeConnection(peerName);
    }

    /**
     * Used to track the connection to a peer.
     *
     * @param peerName The Peer's name for the connection being managed.
     * @param peerHandlerThread The thread running that communicates with the Peer.
     */
    public void addConnection(String peerName, PeerHandlerThread peerHandlerThread) {
        if (this.connectedPeers.containsKey(peerName)) {
            throw new IllegalArgumentException("Peer is already connected.");
        }

        this.connectedPeers.put(peerName, peerHandlerThread);
    }

    /**
     * Stops tracking the connection to a peer.
     *
     * @param peerName The Peer's name for the connection to stop tracking.
     */
    public void removeConnection(String peerName) {
        if (!this.connectedPeers.containsKey(peerName)) {
            throw new IllegalArgumentException("Peer isn't connected.");
        }

        this.connectedPeers.remove(peerName);
    }

    /**
     * Stops tracking a connection to a peer based on the Peer Handler Thread.
     *
     * @param peerHandlerThread The peer handler thread to stop tracking.
     */
    public void removeConnection(PeerHandlerThread peerHandlerThread) {
        for (String peerName : this.connectedPeers.keySet()) {
            if (this.connectedPeers.get(peerName).equals(peerHandlerThread)) {
                this.removeConnection(peerName);
                return;
            }
        }

        throw new IllegalArgumentException("Peer Handler Thread not associated with connected Peer Name.");
    }

    /**
     * Sends a message to the given Peer. Peer must be connected.
     *
     * @param peerName A connected Peer to send the message to.
     * @param message The message to send.
     */
    public void sendMessageToPeer(String peerName, String message) {
        if (!this.connectedPeers.containsKey(peerName)) {
            throw new IllegalArgumentException(peerName + " is not a connected Peer.");
        }

        this.connectedPeers.get(peerName).sendMessage(message);
    }

    /*
        Returns the port this Peer is listening on for other Peer connections.
     */
    private int getListeningPort() {
        return this.peerListenerThread.getPort();
    }

    /*
        Sends a request to the given Rendezvous server. Returns the server response.
     */
    private static RendezvousMessage sendMessageToRendezvous(InetSocketAddress rendezvousServer, RendezvousMessage request) throws IOException {
        Socket rendezvousSocket = new Socket();
        rendezvousSocket.connect(rendezvousServer);
        ObjectOutputStream toServer = new ObjectOutputStream(rendezvousSocket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(rendezvousSocket.getInputStream());

        toServer.writeObject(request);
        RendezvousMessage response = null;

        try {
            response = (RendezvousMessage) fromServer.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        rendezvousSocket.close();

        return response;
    }
}