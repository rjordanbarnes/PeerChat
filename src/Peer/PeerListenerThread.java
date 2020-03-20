package Peer;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for incoming peer connections.
 */
public class PeerListenerThread implements Runnable{
    Peer peer;
    int port;
    ServerSocket serverSocket;
    volatile boolean running;

    /**
     * Starts listening for peers on a random port.
     *
     * @param peer The peer to start listening.
     */
    public PeerListenerThread(Peer peer) {
        // Random port
        this(peer, 0);
    }

    /**
     * Starts listening for peers on a specific port.
     *
     * @param peer The peer to start listening.
     * @param port The port to start listening on.
     */
    public PeerListenerThread(Peer peer, int port) {
        this.peer = peer;
        this.port = port;
        this.running = false;
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.running = true;

            System.out.println("Peer Listener listening on " + this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getLocalPort());

            while (this.running) {
                Socket peerSocket = this.serverSocket.accept();

                // We've been connected to, get the peer's name
                ObjectInputStream fromPeer = new ObjectInputStream(peerSocket.getInputStream());
                PeerMessage peerNameMessage = (PeerMessage) fromPeer.readObject();

                if (!peerNameMessage.messageType.equals(PeerMessage.MessageType.NAME)) {
                    // Didn't receive a peer name first, end this connection.
                    System.out.println("Didn't receive name first, ending connection.");
                    peerSocket.close();
                    break;
                }

                String peerName = peerNameMessage.messagePayload;
                System.out.println("\n" + peerName + " connected.");

                this.peer.peerConnected(peerName);

                // Start up a thread to continue communication with peer. This new thread will eventually close the socket.
                PeerHandlerThread peerHandlerThread = new PeerHandlerThread(this.peer, peerSocket);
                Thread thread = new Thread(peerHandlerThread);
                thread.start();
                this.peer.addConnection(peerName, peerHandlerThread);
            }

            this.serverSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getPort() {
        while (!this.running) {
        }
        return this.serverSocket.getLocalPort();
    }
}
