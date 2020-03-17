package Applications;

import Discovery.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public class PeerConsoleApp {
    public static void main(String[] args) throws IOException {
        Peer peer = new Peer();
        peer.joinRendezvous(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), args[2]);
        Map<String, InetAddress> peerMap = peer.refreshAndGetPeerMap();

        for (String peerName : peerMap.keySet()) {
            System.out.println(peerName + " -> " + peerMap.get(peerName));
        }
    }
}
