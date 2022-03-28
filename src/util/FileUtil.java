package util;

import java.io.File;
import java.nio.file.Paths;

public class FileUtil {
    public static String listCurrDirFiles(File curDir) {
        String list = "";
        StringBuilder sb = new StringBuilder(list);
        File[] filesList = curDir.listFiles();

        if (filesList != null) {
            for (File f : filesList) {
                sb.append(f.getName()).append("\n");
            }
        }

        return sb.toString();
    }

    public static void fileAccess(){
        String PATH = "/remote/dir/server/";
        String directoryName = PATH;
        File directory = new File(directoryName);
        if (!directory.exists()){
            directory.mkdirs();
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
        return Paths.get(currentDir).getParent().toString();
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
