package util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static String listDirFiles(File dir) {
        String list = "";
        StringBuilder sb = new StringBuilder(list);
        File[] filesList = dir.listFiles();

        if (filesList != null) {
            for (File f : filesList) {
                sb.append(f.getName()).append("\n");
            }
        }

        return sb.toString();
    }

    public static String getNextCWD(String targetDir, String currDir) throws IOException {
        boolean validDir = false;
        if (targetDir.contains("..")) {
            targetDir = currDir + "\\" + targetDir;
        }

        File file = new File(targetDir);

        if (!file.isDirectory() || !file.exists()) {
            file = new File(currDir + "\\" + targetDir);

            if (!file.isDirectory() || !file.exists()) {
                System.out.println("Error: no directory '" + targetDir + "' found!");
            } else {
                validDir = true;
            }

        } else {
            validDir = true;
        }

        if (validDir) {
            currDir = file.getCanonicalPath();
            System.setProperty("user.dir", currDir);
        }

        return currDir;
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
