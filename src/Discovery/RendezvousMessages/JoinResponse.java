package Discovery.RendezvousMessages;

import java.io.Serializable;

public class JoinResponse implements RendezvousMessage, Serializable {
    public boolean isSuccessful;

    public JoinResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    @Override
    public String getMethod() {
        return "JoinResponse";
    }
}
