package protocol.failover.redundancy;

import java.io.Serializable;

/**
 * This enum is used to represent the data type of the failover.
 * FILE - Failover in a file.
 * DB_DML - Failover in a database.
 */

public enum FailoverDataTypeEnum implements Serializable {
    FILE, DB_DML
}
