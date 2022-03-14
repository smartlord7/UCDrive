package server;

import java.io.*;

public class Server{
    public static void run() throws IOException {
        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
        System.out.println("Choose command channel port: ");
        int commandPort = Integer.parseInt(in.readLine());
        new Thread(new ClientCommandHandler(commandPort)).start();
        System.out.println("Choose data channel port: ");
        int dataPort = Integer.parseInt(in.readLine());
        new Thread(new ClientDataHandler(dataPort)).start();
    }
}

