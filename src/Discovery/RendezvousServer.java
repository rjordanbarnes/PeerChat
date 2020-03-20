package Discovery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Server that tracks peers so that peers can find each other.
 */
public class RendezvousServer {
    public static int DEFAULT_PORT = 19816;

    // Port the server listens on.
    private int port;

    // Maps PeerName -> IP/Port
    private Map<String, InetSocketAddress> peerMap;

    // Maps IP/Port -> PeerName
    private Map<InetSocketAddress, String> connectionMap;

    public RendezvousServer() {
        this(DEFAULT_PORT);
    }

    public RendezvousServer(int port) {
        this.peerMap = new HashMap<>();
        this.connectionMap = new HashMap<>();
        this.port = port;
    }

    /**
     * Starts the Rendezvous Server.
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        boolean running = true;
        System.out.println("Rendezvous Server listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

        while (running) {
            Socket peerSocket = serverSocket.accept();

            Thread thread = new Thread(new RendezvousServerThread(peerSocket, this));
            thread.start();
        }

        serverSocket.close();
    }

    /**
     * Adds the given peer to the Rendezvous Server.
     *
     * @param peerName The name of the peer to add.
     * @param peerAddress The address of the peer to add.
     */
    public synchronized void addPeer(String peerName, InetSocketAddress peerAddress) {
        if (peerName == null || peerName.equals("")) {
            throw new IllegalArgumentException("Peer name must not be blank.");
        }

        if (peerAddress == null) {
            throw new IllegalArgumentException("Peer address must not be blank.");
        }

        if (this.peerMap.containsKey(peerName)) {
            if (!this.peerMap.get(peerName).equals(peerAddress)) {
                // Peer name is already taken.
                throw new IllegalArgumentException("Peer name " + peerName + " is already in-use.");
            }
            // This is just a duplicate request, do nothing special.
        }

        if (this.connectionMap.containsKey(peerAddress)) {
            // Peer is already added, remove old information first.
            String oldPeerName = this.connectionMap.get(peerAddress);
            this.peerMap.remove(oldPeerName);
            this.connectionMap.remove(peerAddress);
        }

        this.peerMap.put(peerName, peerAddress);
        this.connectionMap.put(peerAddress, peerName);
        System.out.println(this.peerMap);
    }

    /**
     * Removes the given peer from the Rendezvous Server.
     *
     * @param peerAddress The address of the peer to remove.
     */
    public synchronized void removePeer(InetSocketAddress peerAddress) {
        if (peerAddress == null) {
            throw new IllegalArgumentException("Peer address must not be blank.");
        }

        String peerName = this.connectionMap.get(peerAddress);
        this.connectionMap.remove(peerAddress);
        this.peerMap.remove(peerName);
        System.out.println(this.peerMap);
    }

    /**
     * Returns the name of the peer associated with the given address.
     *
     * @param peerAddress The peer's address to lookup.
     * @return The peer's associated name.
     */
    public synchronized String getPeerNameFromAddress(InetSocketAddress peerAddress) {
        return this.connectionMap.get(peerAddress);
    }

    /**
     * Returns the current Peer map.
     *
     * @return The current peer map.
     */
    public synchronized Map<String, InetSocketAddress> getPeerMap() {
        return peerMap;
    }
}
