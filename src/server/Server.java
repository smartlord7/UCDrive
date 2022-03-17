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
        menu();
        if(String.equals("List Directory")){
            fileAccess();
        }
        if(String.equals("Get Disk Space")){
            getDiskSpace();
        }
    }
    public static String menu(){
        return """
                \t\t\t\t       Client
                \t\t\t\t\t   MENU
                \t\t\t ________________________________
                \t\t\t|                                |
                \t\t\t|1 -> REGISTER  |
                \t\t\t|                                |
                \t\t\t|2 -> LOGIN    |
                \t\t\t|                                |
                \t\t\t|3 -> UPLOAD                 |
                \t\t\t|                                |
                \t\t\t|4 -> DOWNLOAD        |
                \t\t\t|________________________________|
                \s
                \t\t\t5 - EXIT
                                    \s""";
    }
    private static void showFiles(File curDir) {

        File[] filesList = curDir.listFiles();
        for (File f : filesList) {
            if (f.isDirectory())
                showFiles(f);
            if (f.isFile()) {
                System.out.println(f.getName());
            }
        }
    }
    public static void fileAccess(){
        String PATH = "/remote/dir/server/";
        String directoryName = PATH;
        File directory = new File(directoryName);
        if (!directory.exists()){
            directory.mkdirs();
            showFiles(directory);
        }else{
            showFiles(directory);
        }
    }

    public static void getDiskSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();
        long freeSpace = curDir.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("Total space: "+totalSpace);
        System.out.println("Used space: "+usedSpace);

    }
}

