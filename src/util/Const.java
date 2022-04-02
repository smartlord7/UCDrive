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

/**
 * Class that has all the constants.
 */

public class Const {

    // region Const

    public static final String COLOR_RESET = "\033[0m";
    public static final String COLOR_BLUE = "\033[0;34m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String APP_NAME = "UCDrive";
    public static final String CMD_SYMBOL = Const.COLOR_GREEN + "$ " + Const.COLOR_RESET;
    public static final String USERS_FOLDER_NAME = "users";
    public static final String APP_VERSION = "1.0";
    public static final String PASSWORD_HASH_ALGORITHM = "SHA-256";
    public static final String FILE_CONTENT_CHECKSUM_ALGORITHM = "MD5";
    public static final int UPLOAD_FILE_CHUNK_SIZE = 512;
    public static final int DOWNLOAD_FILE_CHUNK_SIZE = 512;
    public static final int UDP_BUFFER_SIZE = 4096;

    // endregion Const

}
