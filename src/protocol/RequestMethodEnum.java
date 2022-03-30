package protocol;

import java.io.Serializable;

public enum RequestMethodEnum implements Serializable {
    USER_CREATE,
    USER_AUTHENTICATION,
    USER_CHANGE_PASSWORD,
    USER_LIST_SERVER_FILES,
    USER_CHANGE_CWD,
    USER_DOWNLOAD_FILES,
    USER_UPLOAD_FILES
}
