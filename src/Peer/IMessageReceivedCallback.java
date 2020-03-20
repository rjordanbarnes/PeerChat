package Peer;

public interface IMessageReceivedCallback {
    void callback(String fromPeerName, String messageReceived);
}
