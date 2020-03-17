package Applications;

import Discovery.RendezvousServer;

import java.io.IOException;

public class RendezvousServerConsoleApp {
    public static void main(String[] args) {
        RendezvousServer server;

        if (args.length > 0) {
            server = new RendezvousServer(Integer.parseInt(args[0]));
        } else {
            server = new RendezvousServer();
        }

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
