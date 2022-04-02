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

package server.struct;

import protocol.failover.redundancy.FailoverData;
import sync.SyncObj;
import util.FileMetadata;

import java.util.concurrent.BlockingQueue;

/**
 * Class that the server user session methods.
 */
public class ServerUserSession {

    // region Private properties

    private int userId;
    private String currentDir;
    private FileMetadata fileMetadata;
    private SyncObj syncObj;
    private BlockingQueue<FailoverData> dataToSync;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public ServerUserSession() {
        this.syncObj = new SyncObj();
    }

    /**
     * Constructor method
     * @param userId is the user id.
     * @param lastSessionDir is the last session accessed directory.
     * @param dataToSync is the data to sync with the secondary server.
     */
    public ServerUserSession(int userId, String lastSessionDir, BlockingQueue<FailoverData> dataToSync) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
        this.dataToSync = dataToSync;
        this.syncObj = new SyncObj();
    }

    // endregion Public methods

    // region Getters and Setters

    public synchronized int getUserId() {
        return userId;
    }

    public synchronized void setUserId(int userId) {
        this.userId = userId;
    }

    public synchronized String getCurrentDir() {
        return currentDir;
    }

    public synchronized void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public synchronized FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public synchronized void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public synchronized SyncObj getSyncObj() {
        return syncObj;
    }

    public synchronized void setSyncObj(SyncObj syncObj) {
        this.syncObj = syncObj;
    }

    public BlockingQueue<FailoverData> getDataToSync() {
        return dataToSync;
    }

    public void setDataToSync(BlockingQueue<FailoverData> dataToSync) {
        this.dataToSync = dataToSync;
    }

    // endregion Getters and Setters

}
