package Peer;

import java.io.Serializable;

public class PeerMessage implements Serializable {
    public MessageType messageType;
    public String messagePayload;

    public PeerMessage(MessageType messageType, String messagePayload) {
        this.messageType = messageType;
        this.messagePayload = messagePayload;
    }

    public enum MessageType {
        CHAT, // A Chat.Chat message
        END,  // Ending the connection
        NAME  // The peer's name
    }
}
