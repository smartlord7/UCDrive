/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package util;

import datalayer.enumerate.FileOperationEnum;


/**
 * Class that has the file metadata and it's properties.
 */
public class FileMetadata {

    // region Private properties

    private String fileName;
    private int fileSize;
    private FileOperationEnum op;

    // endregion Private properties

    // region Public methods

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

    // endregion Public methods

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
