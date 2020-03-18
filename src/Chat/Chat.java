package Chat;

import Peer.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

public class Chat {
    private Peer peer = new Peer();

    /**
     * Joins the Rendezvous Server that this Chat app uses to discover peers.
     *
     * @param rendezvous The Rendezvous Server's IP to connect to.
     * @param peerName The name the Peer will use on the Rendezvous Server.
     */
    public void joinRendezvous(InetAddress rendezvous, String peerName) throws IOException {
        this.peer.joinRendezvous(rendezvous, peerName);
    }

    /**
     * Joins the Rendezvous Server that this Chat app uses to discover peers.
     *
     * @param rendezvous The Rendezvous Server's IP to connect to.
     * @param port The Rendezvous Server's Port to connect to.
     * @param peerName The name the Peer will use on the Rendezvous Server.
     */
    public void joinRendezvous(InetAddress rendezvous, int port, String peerName) throws IOException {
        this.peer.joinRendezvous(rendezvous, port, peerName);
    }

    /**
     * Gets the list of known peer names from the Rendezvous Server.
     *
     * @return A Set of peer names.
     */
    public Set<String> getKnownPeers() throws IOException {
        return this.peer.refreshAndGetPeerMap().keySet();
    }

    /**
     * Starts a chat with the given peer.
     *
     * @param peerName The peer to connect to.
     */
    public void connectToPeer(String peerName) throws IOException {
        this.peer.connectToPeer(peerName);
    }

    /**
     * Ends the chat with the given peer.
     *
     * @param peerName The peer to disconnect from. Must be a peer that is currently connected.
     */
    public void disconnectFromPeer(String peerName) {
        this.peer.disconnectFromPeer(peerName);
    }

    /**
     * Sends a message to the given peer.
     *
     * @param peerName The peer to send the message to.
     * @param message The message to send to the peer.
     */
    public void sendMessage(String peerName, String message) {
        this.peer.sendMessageToPeer(peerName, message);
    }
}