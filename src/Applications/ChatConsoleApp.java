package Applications;

import Chat.Chat;
import Discovery.RendezvousServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Set;

public class ChatConsoleApp {
    public static void main(String[] args) throws IOException {
        InetAddress rendezvousServerAddress = InetAddress.getByName("localhost");
        int rendezvousServerPort = RendezvousServer.DEFAULT_PORT;
        String peerName = "Jordan";

        if (args.length > 0) {
            rendezvousServerAddress = InetAddress.getByName(args[0]);
            rendezvousServerPort = Integer.parseInt(args[1]);
            peerName = args[2];
        }

        // Create the chat and join the rendezvous.
        Chat chat = new Chat();
        try {
            chat.joinRendezvous(rendezvousServerAddress, rendezvousServerPort, peerName);
        } catch (Exception ex) {
            System.out.println("Unable to connect to Rendezvous server " + rendezvousServerAddress + ":" + rendezvousServerPort);
            System.exit(0);
        }


        // Refresh the peer list.
        Set<String> knownPeers = chat.getKnownPeers();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println(knownPeers);
        System.out.print("Enter a command (get, connect, disconnect, send): ");

        while((input = reader.readLine()) != null) {
            String[] split = input.split(" ");

            try {
                switch (split[0]) {
                    case "get":
                        knownPeers = chat.getKnownPeers();
                        break;
                    case "connect":
                        chat.connectToPeer(split[1]);
                        break;
                    case "disconnect":
                        chat.disconnectFromPeer(split[1]);
                        break;
                    case "send":
                        chat.sendMessage(split[1], split[2]);
                        break;
                    default:
                        System.out.println("Unknown command " + split[0]);
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            System.out.println("\nKnown Peers: " + knownPeers);
            System.out.print("Enter a command (get, connect, disconnect, send): ");
        }
    }
}
