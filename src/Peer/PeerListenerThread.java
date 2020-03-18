package Peer;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for incoming peer connections.
 */
public class PeerListenerThread implements Runnable{
    Peer peer;
    ServerSocket serverSocket;
    volatile boolean running;

    public PeerListenerThread(Peer peer) {
        this.peer = peer;
        this.running = false;
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(0);
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
