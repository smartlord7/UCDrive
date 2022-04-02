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

package protocol.failover.redundancy;

import java.io.Serializable;

/**
 * Class that has the failover feedback methods.
 */
public class FailoverFeedback implements Serializable {

    // region Private properties

    private int id;
    private FailoverFeedbackTypeEnum feedback;

    // endregion Private properties

    // region Public methods

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

    // endregion Public methods

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
