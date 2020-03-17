package Discovery;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class RendezvousServer {
    public static int DEFAULT_PORT = 1234;

    // Port the server listens on.
    private int port;

    // Maps PeerName -> IP/Port
    private Map<String, SocketAddress> peerMap;

    // Maps IP/Port -> PeerName
    private Map<SocketAddress, String> connectionMap;

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
    public synchronized void addPeer(String peerName, SocketAddress peerAddress) {
        if (this.peerMap.containsKey(peerName)) {
            // Peer name is already taken.
            throw new IllegalArgumentException("Peer name " + peerName + " already in-use.");
        }

        if (this.connectionMap.containsKey(peerAddress)) {
            // Peer is already added, remove old information first.
            String oldPeerName = this.connectionMap.get(peerAddress);
            this.peerMap.remove(oldPeerName);
            this.connectionMap.remove(peerAddress);
        }

        this.peerMap.put(peerName, peerAddress);
        this.connectionMap.put(peerAddress, peerName);
    }

    /**
     * Removes the given peer from the Rendezvous Server.
     *
     * @param peerAddress The address of the peer to remove.
     */
    public synchronized void removePeer(SocketAddress peerAddress) {
        String peerName = this.connectionMap.get(peerAddress);
        this.connectionMap.remove(peerAddress);
        this.peerMap.remove(peerName);
    }

    /**
     * Returns the name of the peer associated with the given address.
     *
     * @param peerAddress The peer's address to lookup.
     * @return The peer's associated name.
     */
    public synchronized String getPeerNameFromAddress(SocketAddress peerAddress) {
        return this.connectionMap.get(peerAddress);
    }

    /**
     * Returns the current Peer map.
     *
     * @return The current peer map.
     */
    public synchronized Map<String, SocketAddress> getPeerMap() {
        return peerMap;
    }
}
