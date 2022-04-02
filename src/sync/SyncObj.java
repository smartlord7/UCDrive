package sync;

/**
 * Class that defines the SyncObj and its methods.
 */
public class SyncObj {

    // region Private properties

    private boolean active;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public SyncObj() {
    }

    /**
     * Constructor method.
     * @param active is the flag to check if the object associated with this monitor is active.
     */
    public SyncObj(boolean active) {
        this.active = active;
    }

    /**
     * Method that checks if the object is active.
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method that sets the object active.
     * @param active is the flag that check if the object associated with this monitor is active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Method that simulates a condition variable.
     * @param inverted is a flag to specify if it should execute while active or not.
     * @throws InterruptedException - if the method is interrupted (i.e. manually stopping the program)
     */
    public void wait(boolean inverted) throws InterruptedException {
        synchronized (this) {
            if (inverted) {
                while (this.active) {
                    this.wait();
                }
            } else {
                while (!this.active) {
                    this.wait();
                }
            }
        }
    }

    /**
     * Method that changes the active flag.
     */
    public void change() {
        synchronized (this) {
            this.active = !this.active;
            this.notifyAll();
        }
    }

    // endregion Public methods

}
