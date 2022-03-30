package util;

import java.io.*;

import static sun.nio.ch.IOStatus.EOF;

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

    public static byte[] substring(byte[] array, int start, int end) {
        assert end > start;
        int length = (end - start);

        byte[] newArray = new byte[length];
        System.arraycopy(array, start, newArray, 0, length);

        return newArray;
    }

    public static void receiveFileByChunks(InputStream in, FileMetadata fileMetadata, int chunkSize) throws IOException {
        int totalRead = 0;
        int bytesRead;
        int fileSize = 0;
        byte[] buffer = new byte[chunkSize];
        String name;
        FileMetadata fileMeta = null;
        FileOutputStream fileWriter = null;

        bytesRead = 0;
        while ((bytesRead = in.read(buffer,0, chunkSize)) != EOF)
        {
            if (fileMeta == null) {
                if (fileMeta == null || fileMeta.getFileSize() == 0) {
                    continue;
                }

                fileSize = fileMeta.getFileSize();
                fileWriter = new FileOutputStream(fileMetadata.getFileName());
            }

            if (bytesRead > fileMeta.getFileSize()) {
                buffer = FileUtil.substring(buffer, 0, fileSize);
            }

            totalRead += bytesRead;
            fileWriter.write(buffer);

            if (totalRead >= fileSize) {
                fileWriter.close();
                fileMeta = null;
            }
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

    public static long getFreeSpace(File dir){
        if (!dir.exists() || !dir.isDirectory()) {
            return -1;
        }
        return dir.getFreeSpace();
    }
}
