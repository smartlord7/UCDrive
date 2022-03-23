package server;

import businesslayer.User.UserDAO;
import datalayer.enumerate.FilePermissionEnum;

import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;

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
    private static String menu(){
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
    private static int getFilePerms(String curDir,int id) throws SQLException {
        HashSet<FilePermissionEnum> perms = UserDAO.getDirectoryPermissions(id,curDir);
        if(perms.contains(FilePermissionEnum.READ)){
            System.out.println("User has read Permissions");
            return 0;
        }else if(perms.contains(FilePermissionEnum.WRITE)){
            System.out.println("User has write Permissions");
            return 1;
        }else if(perms.contains(FilePermissionEnum.READ_WRITE)){
            System.out.println("User has Read and Write Permissions");
            return 2;
        }
        else{
            return 3;
        }
    }

    private static void downloadFiles(File curDir) throws SQLException {
        showFiles(curDir);
        int id = 4;
        int perm = getFilePerms(curDir.getPath(), id);
        System.out.println("Select the file to download: ");
        if(perm == 0 || perm == 2 && getFreeSpace(curDir) > curDir.length()){
            System.out.println("Permission to download");
            download();
        }else if(perm == 1){
            System.out.println("No permission to download");
        }else if(getFreeSpace(curDir) < curDir.length()){
            System.out.println("No space left");
        }
    }
    private static void uploadFiles(File curDir) throws SQLException    {
        showFiles(curDir);
        int id = 4;
        int perm = getFilePerms(curDir.getPath(), id);
        long space = getFreeSpace(curDir);
        System.out.println("Select the file to upload: ");
        if(perm == 1 || perm == 2 && getFreeSpace(curDir) > curDir.length() ){
            System.out.println("Permission to upload");
            upload();
        }else if(perm == 0){
            System.out.println("No permission to upload");
        }else if(getFreeSpace(curDir) < curDir.length()){
            System.out.println("No space left");
        }
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


    private static void fileAccess(){
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
    private static void backTrack(){
        String curDir = System.getProperty("user.dir");

    }
    private static long getTotalSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();

        System.out.println("Total space: "+totalSpace);

        return totalSpace;
    }
    private static long getUsedSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();
        long freeSpace = curDir.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("Used space: "+usedSpace);
        return usedSpace;
    }
    private static long getFreeSpace(File curDir){
        long freeSpace = curDir.getFreeSpace();

        System.out.println("Free space: "+freeSpace);
        return freeSpace;
    }
}

