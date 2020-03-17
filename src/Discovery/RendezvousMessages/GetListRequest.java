package Discovery.RendezvousMessages;

import java.io.Serializable;

public class GetListRequest implements RendezvousMessage, Serializable {
    @Override
    public String getMethod() {
        return "GetListRequest";
    }
}
