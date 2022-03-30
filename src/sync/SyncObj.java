package sync;

import util.FileMetadata;

import java.util.ArrayList;

public class SyncObj {
    private boolean use;
    private boolean active;
    private FileMetadata fileMeta;

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

    public FileMetadata getFileInfo() {
        return fileMeta;
    }

    public void setFileInfo(FileMetadata fileMeta) {
        this.fileMeta = fileMeta;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public void wait(boolean inverted) throws InterruptedException {
        synchronized (this) {
            if (inverted) {
                while (!this.active) {
                    this.wait();
                }
            } else {
                while (this.active) {
                    this.wait();
                }
            }
        }
    }

    public void broadcast() {
        synchronized (this) {
            this.active = !this.active;
            this.notifyAll();
        }
    }
}
