package Discovery.RendezvousMessages;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Map;

public class GetListResponse implements RendezvousMessage, Serializable {
    public Map<String, InetSocketAddress> peerMap;

    public GetListResponse(Map<String, InetSocketAddress> peerMap) {
        this.peerMap = peerMap;
    }

    @Override
    public String getMethod() {
        return "GetListResponse";
    }
}
