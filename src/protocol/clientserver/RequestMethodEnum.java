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

package protocol.clientserver;

import java.io.Serializable;

// region Public enum

/**
 * This enum is used to represent the request methods that are avaliable trough the following keywords:
 * USER_CREATE - Method to register a user.
 * USER_AUTHENTICATION - Method to authenticate the user.
 * USER_CHANGE_PASSWORD - Method to change the user password.
 * USER_LIST_SERVER_FILES - Method to list the server files.
 * USER_CHANGE_CWD - Method to change the directory.
 * USER_DOWNLOAD_FILE - Method to download a file.
 * USER_UPLOAD_FILE - Method to upload a file.
 * USER_LOGOUT - Method to logout a user.
 */
public enum RequestMethodEnum implements Serializable {
    USER_CREATE,
    USER_AUTHENTICATION,
    USER_CHANGE_PASSWORD,
    USER_LIST_SERVER_FILES,
    USER_CHANGE_CWD,
    USER_DOWNLOAD_FILE,
    USER_UPLOAD_FILE,
    USER_LOGOUT
}

// endregion Public enum
