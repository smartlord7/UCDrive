package server;

import java.io.*;

public class ServerMain {
    public static void run() throws IOException {
        int commandPort;
        int dataPort;
        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));

        System.out.println("Choose command channel port: ");
        commandPort = Integer.parseInt(in.readLine());
        System.out.println("Choose data channel port: ");
        dataPort = Integer.parseInt(in.readLine());
        new Thread(new ClientCommandHandler(commandPort)).start();
        new Thread(new ClientDataHandler(dataPort)).start();
    }
}
