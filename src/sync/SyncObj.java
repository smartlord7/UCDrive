package sync;

public class SyncObj {
    private boolean active;

    public SyncObj() {
    }

    public SyncObj(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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

    public void change() {
        synchronized (this) {
            this.active = !this.active;
            this.notifyAll();
        }
    }
}
