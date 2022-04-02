package protocol.clientserver;

import java.io.Serializable;

// region Public enum

/**
 * This enum is used to represent the response status with the following status:
 * ERROR - An error occurred.
 * SUCCESS - Everything executed successfully.
 * UNAUTHORIZED - Missing permissions.
 */
public enum ResponseStatusEnum implements Serializable {
    ERROR, SUCCESS, UNAUTHORIZED
}

// endregion Public enum