package protocol.failover.redundancy;

import java.io.Serializable;

public enum FailoverFeedbackTypeEnum implements Serializable {
    ACK, NACK, REPEATED
}
