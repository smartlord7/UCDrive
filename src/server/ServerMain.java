package server;

import sync.UserSessions;
import util.Const;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {
    private static int commandPort;
    private static int dataPort;

    private static void init() throws IOException {
        Path p = Paths.get(Const.USERS_FOLDER_NAME);
        if (!Files.exists(p)) {
            Files.createDirectory(p);
        }

        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
        //System.out.println("Choose command channel port: ");
        commandPort = 8000;
        //System.out.println("Choose data channel port: ");
        dataPort = 8001;
    }

    public static void run() throws IOException {
        UserSessions sessions = new UserSessions();

        init();

        new Thread(new ServerCommandChannelHandler(commandPort, sessions)).start();
        new Thread(new ServerDataChannelHandler(dataPort, sessions)).start();
    }
}
