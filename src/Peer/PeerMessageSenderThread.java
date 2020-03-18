package Peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sends messages to a Peer.
 */
public class PeerMessageSenderThread implements Runnable {

    ObjectOutputStream toPeer;

    // Contains the messages to be sent to the Peer.
    private BlockingQueue<PeerMessage> messages;

    public PeerMessageSenderThread(ObjectOutputStream toPeer) {
        this.toPeer = toPeer;
        this.messages = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                PeerMessage message = messages.take();

                if (message.messageType.equals(PeerMessage.MessageType.END)) {
                    break;
                }

                this.toPeer.writeObject(message);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendChatMessage(String message) {
        try {
            messages.put(new PeerMessage(PeerMessage.MessageType.CHAT, message));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void endChat() {
        try {
            messages.put(new PeerMessage(PeerMessage.MessageType.END, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
