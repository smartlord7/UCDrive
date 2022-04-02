package util;

import java.io.*;
import java.util.PropertyPermission;

public class FileUtil {
    /**
     * Method used to list the directory files.
     * @param dir the directory of the files to be listed.
     * @return the files in the directory.
     */
    public static String listDirFiles(File dir) {
        String list = "\n";
        StringBuilder sb = new StringBuilder(list);
        File[] filesList = dir.listFiles();

        if (filesList != null) {
            for (File f : filesList) {
                sb.append(f.getName()).append("\n");
            }
        }

        return sb.toString().strip();
    }

    /**
     * Method to get the next directory
     * @param targetDir the directory to go.
     * @param currDir the current directory.
     * @return the targeted directory as the current directory.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     */
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
        }

        return currDir;
    }

    /**
     * Method used to store the substrings.
     * @param array is the array where the substrings will be stored.
     * @param start is the start of the string
     * @param end is the end of the string.
     * @return the array with the substrings.
     */
    public static byte[] substring(byte[] array, int start, int end) {
        assert end > start;
        int length = (end - start);

        byte[] newArray = new byte[length];
        System.arraycopy(array, start, newArray, 0, length);

        return newArray;
    }

    /**
     * Method to parse the directories.
     * @param line is the line containing the directory.
     * @param dir is the directory to be parsed.
     * @return the parsed directory.
     */
    public static String parseDir(String line, String dir) {
        if (dir.startsWith("'")) {
            if (!line.strip().endsWith("'")) {
                System.out.println("Error: malformed command '" + line + "'");
                return null;
            }

            try {
                dir = line.split("'")[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Error: malformed command '" + line + "'");
                return null;
            }
        }

        return dir;
    }

    /**
     * Method used to get the free,total and used disk space.
     * @param curDir the current directory.
     */
    public static void getDiskSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();
        long freeSpace = curDir.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("Total space: "+totalSpace);
        System.out.println("Used space: "+usedSpace);
    }

    /**
     * Method used to get the total space from a directory.
     * @param curDir is the current directory.
     * @return the total space of the current directory.
     */
    public static long getTotalSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();

        System.out.println("Total space: "+totalSpace);

        return totalSpace;
    }

    /**
     * Method used to get the used space of the current directory.
     * @param curDir is the current directory.
     * @return the used space of the current directory.
     */
    public static long getUsedSpace(File curDir){
        long totalSpace = curDir.getTotalSpace();
        long freeSpace = curDir.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        System.out.println("Used space: "+usedSpace);
        return usedSpace;
    }

    /**
     * Method used to get the free space of the directory.
     * @param dir is the current directory.
     * @return the free space of the current directory.
     */
    public static long getFreeSpace(File dir){
        if (!dir.exists() || !dir.isDirectory()) {
            return -1;
        }
        return dir.getFreeSpace();
    }
}
