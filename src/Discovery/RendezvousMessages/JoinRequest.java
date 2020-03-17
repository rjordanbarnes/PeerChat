package Discovery.RendezvousMessages;

import java.io.Serializable;

public class JoinRequest implements RendezvousMessage, Serializable {
    public String peerName;
    public int peerPort;

    public JoinRequest(String peerName, int peerPort) {
        this.peerName = peerName;
        this.peerPort = peerPort;
    }

    @Override
    public String getMethod() {
        return "JoinRequest";
    }
}
