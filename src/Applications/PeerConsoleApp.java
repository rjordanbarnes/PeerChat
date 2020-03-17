package Applications;

import Discovery.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Map;

public class PeerConsoleApp {
    public static void main(String[] args) throws IOException {
        InetAddress rendezvousServerAddress = InetAddress.getByName(args[0]);
        int rendezvousServerPort = Integer.parseInt(args[1]);
        String peerName = args[2];
        int peerPort = Integer.parseInt(args[3]);

        Peer peer = new Peer(peerPort);
        peer.joinRendezvous(rendezvousServerAddress, rendezvousServerPort, peerName);
        Map<String, SocketAddress> peerMap = peer.refreshAndGetPeerMap();

        for (String name : peerMap.keySet()) {
            System.out.println(name + " -> " + peerMap.get(name));
        }
    }
}
