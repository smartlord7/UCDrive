package protocol.failover.redundancy;

import java.io.Serializable;

public class FailoverFeedback implements Serializable {
    private int id;
    private FailoverFeedbackTypeEnum feedback;

    public FailoverFeedback() {
    }

    public FailoverFeedback(int id, FailoverFeedbackTypeEnum feedback) {
        this.id = id;
        this.feedback = feedback;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FailoverFeedbackTypeEnum getFeedback() {
        return feedback;
    }

    public void setFeedback(FailoverFeedbackTypeEnum feedback) {
        this.feedback = feedback;
    }
}
