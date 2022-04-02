package util;

import datalayer.enumerate.FileOperationEnum;

public class FileMetadata {
    private String fileName;
    private int fileSize;
    private FileOperationEnum op;

    /**
     * Constructor method.
     */
    public FileMetadata() {
    }

    /**
     * Constructor method
     * @param fileName is the file name.
     * @param fileSize is the file size.
     */
    public FileMetadata(String fileName, int fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // region Getters and Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public FileOperationEnum getOp() {
        return op;
    }

    public void setOp(FileOperationEnum op) {
        this.op = op;
    }

    // endregion Getters and Setters

}
