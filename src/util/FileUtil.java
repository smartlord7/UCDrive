package util;

import java.io.File;

public class FileUtil {
    public static void showFiles(File curDir) {
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

    public static String backDir(String currentDir){
        StringBuilder newDir = new StringBuilder();
        String[] parts = currentDir.split("/");

        for (int i = 0; i < parts.length - 1; i++) {
            newDir.append(parts[i]).append("/");
        }

        return newDir.toString();
    }

    public static long getTotalSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();

        System.out.println("Total space: "+totalSpace);

        return totalSpace;
    }

    public static long getUsedSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();
        long freeSpace = curDir.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("Used space: "+usedSpace);
        return usedSpace;
    }

    public static long getFreeSpace(File curDir){
        long freeSpace = curDir.getFreeSpace();

        System.out.println("Free space: "+freeSpace);
        return freeSpace;
    }
}
