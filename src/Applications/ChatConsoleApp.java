package Applications;

import Chat.Chat;
import Discovery.RendezvousServer;
import Peer.IMessageReceivedCallback;

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
        int peerPort = 0;

        if (args.length > 0) {
            rendezvousServerAddress = InetAddress.getByName(args[0]);
            rendezvousServerPort = Integer.parseInt(args[1]);
            peerName = args[2];

            if (args.length > 3) {
                // Optional peer port to use for listening.
                peerPort = Integer.parseInt(args[3]);
            }
        }

        // Create the chat and join the rendezvous.
        Chat chat = new Chat(peerPort);
        chat.onMessageReceived((fromPeerName, messageReceived) -> System.out.println("\n" + fromPeerName + ": " + messageReceived));
        try {
            chat.joinRendezvous(rendezvousServerAddress, rendezvousServerPort, peerName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(0);
        }


        // Refresh the peer list.
        Set<String> knownPeers = chat.getKnownPeers();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println("\nKnown Peers: " + knownPeers);
        System.out.println("Connected Peers: " + chat.getConnectedPeers());
        System.out.print("Enter a command (get, connect, disconnect, send): ");

        while((input = reader.readLine()) != null) {
            String[] split = input.split(" ");

            try {
                switch (split[0]) {
                    case "get":
                        knownPeers = chat.getKnownPeers();
                        break;
                    case "connect":
                        if (split.length < 2) {
                            System.out.println("Usage: connect PEERNAME");
                            break;
                        }

                        chat.connectToPeer(split[1]);
                        break;
                    case "disconnect":
                        if (split.length < 2) {
                            System.out.println("Usage: disconnect PEERNAME");
                            break;
                        }

                        chat.disconnectFromPeer(split[1]);
                        break;
                    case "send":
                        if (split.length < 3) {
                            System.out.println("Usage: send PEERNAME MESSAGE");
                            break;
                        }
                        String message = input.substring(split[0].length() + 1 + split[1].length() + 1);
                        chat.sendMessage(split[1], message);
                        break;
                    default:
                        System.out.println("Unknown command " + split[0]);
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            System.out.println("\nKnown Peers: " + knownPeers);
            System.out.println("Connected Peers: " + chat.getConnectedPeers());
            System.out.print("Enter a command (get, connect, disconnect, send): ");
        }
    }
}
