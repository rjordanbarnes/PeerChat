package Applications;

import Discovery.RendezvousServer;
import Peer.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class PeerConsoleApp {
    public static void main(String[] args) throws IOException {
        InetAddress rendezvousServerAddress = InetAddress.getByName("localhost");
        int rendezvousServerPort = RendezvousServer.DEFAULT_PORT;
        String peerName = "Jordan";

        if (args.length > 0) {
            rendezvousServerAddress = InetAddress.getByName(args[0]);
            rendezvousServerPort = Integer.parseInt(args[1]);
            peerName = args[2];
        }

        Peer peer = new Peer();
        peer.joinRendezvous(rendezvousServerAddress, rendezvousServerPort, peerName);
        Map<String, InetSocketAddress> peerMap = peer.refreshAndGetPeerMap();

        for (String name : peerMap.keySet()) {
            System.out.println(name + " -> " + peerMap.get(name));
        }
    }
}
