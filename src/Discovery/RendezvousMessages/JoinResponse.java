package Discovery.RendezvousMessages;

import java.io.Serializable;

public class JoinResponse implements RendezvousMessage, Serializable {
    public boolean isSuccessful;
    public String reason;

    public JoinResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public JoinResponse(boolean isSuccessful, String reason) {
        this.isSuccessful = isSuccessful;
        this.reason = reason;
    }

    @Override
    public String getMethod() {
        return "JoinResponse";
    }
}
