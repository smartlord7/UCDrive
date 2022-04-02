package businesslayer.base;

import java.io.Serializable;

// region Public enum

/**
 * This enum represents the DAO results trough the following status:
 * SUCCESS - When the database has been modified successfully.
 * ERROR - When an error occurred when modifying the database.
 * IGNORED - When the modifications on the database have been ignored.
 */

public enum DAOResultStatusEnum implements Serializable {
    SUCCESS, ERROR, IGNORED
}

// endregion Public enum
