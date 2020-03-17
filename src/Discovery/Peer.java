package Discovery;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

public class Peer {
    // If currentRendezvous == null then we haven't joined a server
    private SocketAddress currentRendezvous;

    // The name the peer is using on the current Rendezvous Server. null if not connected
    private String peerName;

    // The cached version of the peerMap from the Rendezvous Server
    private Map<String, InetAddress> peerMap;

    public Peer() {
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
     * @param port The port to connect to.
     * @param peerName The name to use on the Rendezvous Server.
     */
    public void joinRendezvous(InetAddress rendezvousAddress, int port, String peerName) throws IOException {
        this.leaveCurrentRendezvous();
        SocketAddress newRendezvousServer = new InetSocketAddress(rendezvousAddress, port);
        RendezvousMessage response = sendMessageToServer(newRendezvousServer, new RendezvousMessage("joinRequest", peerName));

        if (!response.parameter.equals("Success")) {
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

        RendezvousMessage response = sendMessageToServer(this.currentRendezvous, new RendezvousMessage("leaveRequest", null));

        if (!response.parameter.equals("Success")) {
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
    public Map<String, InetAddress> refreshAndGetPeerMap() throws IOException {
        if (this.currentRendezvous == null) {
            // Not connected to a server
            throw new IllegalStateException("Must join a Rendezvous Server first.");
        }

        RendezvousMessage response = sendMessageToServer(this.currentRendezvous, new RendezvousMessage("getListRequest", null));
        this.peerMap = (Map<String, InetAddress>) response.parameter;

        return this.peerMap;
    }

    /**
     * Returns the Peer Map that we most recently obtained from the Rendezvous Server.
     *
     * @return The cached Peer Map. May be outdated.
     */
    public Map<String, InetAddress> getCachedPeerMap() {
        return this.peerMap;
    }

    /*
        Sends a request to the given Rendezvous server. Returns the server response.
     */
    private static RendezvousMessage sendMessageToServer(SocketAddress rendezvousServer, RendezvousMessage request) throws IOException {
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
