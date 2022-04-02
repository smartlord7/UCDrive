package protocol.failover.redundancy;

import java.io.Serializable;

// region Public enum

/**
 * This enum is used to represent the failover feedback with the following status:
 * ACK- Acknowledged.
 * NACK - Not acknowledged.
 * REPEATED - Repeated packet.
 */
public enum FailoverFeedbackTypeEnum implements Serializable {
    ACK, NACK, REPEATED
}

// endregion Public enum
