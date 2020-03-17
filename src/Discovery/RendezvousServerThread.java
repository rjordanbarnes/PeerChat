package Discovery;

import java.io.*;
import java.net.Socket;

public class RendezvousServerThread implements Runnable {

    private Socket peerSocket;
    private RendezvousServer rendezvousServer;

    RendezvousServerThread(Socket peerSocket, RendezvousServer rendezvousServer) {
        this.peerSocket = peerSocket;
        this.rendezvousServer = rendezvousServer;
    }


    @Override
    public void run() {
        try {
            ObjectOutputStream toPeer = new ObjectOutputStream(this.peerSocket.getOutputStream());
            ObjectInputStream fromPeer = new ObjectInputStream(this.peerSocket.getInputStream());

            RendezvousMessage request = (RendezvousMessage) fromPeer.readObject();
            switch (request.method) {
                case "joinRequest":
                    String peerName = (String) request.parameter;

                    try {
                        this.rendezvousServer.addPeer(peerName, peerSocket.getInetAddress());
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                        toPeer.writeObject(new RendezvousMessage("joinRequestResponse", "Failure"));
                        return;
                    }

                    toPeer.writeObject(new RendezvousMessage("joinRequestResponse", "Success"));
                    System.out.println(peerName + " joined the Rendezvous.");
                    break;
                case "leaveRequest":
                    this.rendezvousServer.removePeer(peerSocket.getInetAddress());
                    toPeer.writeObject(new RendezvousMessage("leaveRequestResponse", "Success"));
                    System.out.println(this.rendezvousServer.getPeerName(peerSocket.getInetAddress()) + " left the Rendezvous.");
                    break;
                case "getListRequest":
                    toPeer.writeObject(new RendezvousMessage("getListResponse", this.rendezvousServer.getPeerMap()));
                    System.out.println(this.rendezvousServer.getPeerName(peerSocket.getInetAddress()) + " got the Peer List.");
                    break;
                default:
                    System.out.println("Unknown message type " + request.method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
