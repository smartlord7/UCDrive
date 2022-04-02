package protocol.failover.redundancy;

import java.io.Serializable;

public class FailoverFeedback implements Serializable {
    private int id;
    private FailoverFeedbackTypeEnum feedback;

    /**
     * Constructor method.
     */
    public FailoverFeedback() {
    }

    /**
     * Constructor method.
     * @param id is the to-send information id.
     * @param feedback is the feedback status.
     */
    public FailoverFeedback(int id, FailoverFeedbackTypeEnum feedback) {
        this.id = id;
        this.feedback = feedback;
    }

    // region Getters and Setters

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

    // endregion Getters and Setters

}
