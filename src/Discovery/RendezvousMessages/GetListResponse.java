package Discovery.RendezvousMessages;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Map;

public class GetListResponse implements RendezvousMessage, Serializable {
    public Map<String, SocketAddress> peerMap;

    public GetListResponse(Map<String, SocketAddress> peerMap) {
        this.peerMap = peerMap;
    }

    @Override
    public String getMethod() {
        return "GetListResponse";
    }
}
