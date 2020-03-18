package Peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles new Peer socket connections. Responsible for closing the Socket.
 */
public class PeerHandlerThread implements Runnable {

    Peer peer;
    Socket peerSocket;
    PeerMessageSenderThread peerMessageSender;

    public PeerHandlerThread(Peer peer, Socket peerSocket) {
        this.peer = peer;
        this.peerSocket = peerSocket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream toPeer = new ObjectOutputStream(this.peerSocket.getOutputStream());
            ObjectInputStream fromPeer = new ObjectInputStream(this.peerSocket.getInputStream());

            this.peerMessageSender = new PeerMessageSenderThread(toPeer);
            Thread thread = new Thread(this.peerMessageSender);
            thread.start();

            while(true) {
                PeerMessage receivedMessage = (PeerMessage) fromPeer.readObject();
                System.out.println(receivedMessage.messagePayload);
            }
        } catch (Exception ex) {
            // Socket closed, tell other Thread to stop
            this.peerMessageSender.endChat();
            this.peer.removeConnection(this);
        }
    }

    /**
     * Sends the given message to the Peer associated with this Handler.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        this.peerMessageSender.sendChatMessage(message);
    }

    /**
     * Closes the Socket connection.
     */
    public void closeConnection() {
        try {
            this.peerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
