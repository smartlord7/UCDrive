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

package client.struct;

import java.io.Serializable;

/**
 * Class that holds the user session data in client side.
 */
public class ClientUserSession implements Serializable {

    // region Private properties

    private int userId;
    private String currentDir;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method.
     */
    public ClientUserSession() {
    }

    /**
     * Constructor method.
     * @param userId is the user id.
     * @param lastSessionDir is the last session directory.
     */
    public ClientUserSession(int userId, String lastSessionDir) {
        this.userId = userId;
        this.currentDir = lastSessionDir;
    }

    // endregion Constructors


    // region Public methods

    // region Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    // endregion Getters and Setters

    // endregion Public methods

}
