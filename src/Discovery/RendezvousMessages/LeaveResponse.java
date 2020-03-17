package Discovery.RendezvousMessages;

import java.io.Serializable;

public class LeaveResponse implements RendezvousMessage, Serializable {
    public boolean isSuccessful;

    public LeaveResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    @Override
    public String getMethod() {
        return "LeaveResponse";
    }
}
