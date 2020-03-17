package Discovery.RendezvousMessages;

import java.io.Serializable;

public class LeaveRequest implements RendezvousMessage, Serializable {
    public int peerPort;

    public LeaveRequest(int peerPort) {
        this.peerPort = peerPort;
    }

    @Override
    public String getMethod() {
        return "LeaveRequest";
    }
}
