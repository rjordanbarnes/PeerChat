package Discovery.RendezvousMessages;

import java.io.Serializable;

public class LeaveResponse implements RendezvousMessage, Serializable {
    public boolean isSuccessful;
    public String reason;

    public LeaveResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public LeaveResponse(boolean isSuccessful, String reason) {
        this.isSuccessful = isSuccessful;
        this.reason = reason;
    }

    @Override
    public String getMethod() {
        return "LeaveResponse";
    }
}
