package Discovery;

import Discovery.RendezvousMessages.*;

import java.io.*;
import java.net.InetSocketAddress;
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
            switch (request.getMethod()) {
                case "JoinRequest":
                    JoinRequest joinRequest = (JoinRequest) request;

                    try {
                        this.rendezvousServer.addPeer(joinRequest.peerName, new InetSocketAddress(peerSocket.getInetAddress(), joinRequest.peerPort));
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                        toPeer.writeObject(new JoinResponse(false));
                        return;
                    }

                    toPeer.writeObject(new JoinResponse(true));
                    System.out.println(joinRequest.peerName + " joined the Rendezvous.");
                    break;
                case "LeaveRequest":
                    LeaveRequest leaveRequest = (LeaveRequest) request;
                    InetSocketAddress peerAddress = new InetSocketAddress(peerSocket.getInetAddress(), leaveRequest.peerPort);

                    this.rendezvousServer.removePeer(peerAddress);
                    toPeer.writeObject(new LeaveResponse(true));
                    System.out.println(this.rendezvousServer.getPeerNameFromAddress(peerAddress) + " left the Rendezvous.");
                    break;
                case "GetListRequest":
                    toPeer.writeObject(new GetListResponse(this.rendezvousServer.getPeerMap()));
                    System.out.println(peerSocket.getInetAddress() + " got the Peer List.");
                    break;
                default:
                    System.out.println("Unknown message type " + request.getMethod());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
