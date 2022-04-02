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

import java.util.StringJoiner;

/**
 * Class that has the client state configuration methods.
 */
public class ClientStateConfig {

    // region Private properties

    private boolean isMainServerDown;
    private boolean isMainServerConfigured;
    private boolean isSecondaryServerConfigured;
    private boolean isSecondaryServerDown;
    private boolean isServerConnected;
    private int mainServerCmdPort;
    private int mainServerDataPort;
    private int secondaryServerCmdPort;
    private int secondaryServerDataPort;
    private String mainServerIp;
    private String secondaryServerIp;

    // endregion Private Properties

    // region Constructors

    /**
     * Constructor method.
     */
    public ClientStateConfig() {
    }

    /**
     * Constructor method.
     * @param mainServerCmdPort is the main server command handler port.
     * @param mainServerDataPort is the main server data handler port.
     * @param secondaryServerCmdPort is the secondary server command handler port.
     * @param secondaryServerDataPort is the secondary server data handler port.
     * @param mainServerIp is the main server ip.
     * @param secondaryServerIp is the secondary server ip.
     */
    public ClientStateConfig(int mainServerCmdPort, int mainServerDataPort, int secondaryServerCmdPort, int secondaryServerDataPort, String mainServerIp, String secondaryServerIp) {
        this.mainServerCmdPort = mainServerCmdPort;
        this.mainServerDataPort = mainServerDataPort;
        this.secondaryServerCmdPort = secondaryServerCmdPort;
        this.secondaryServerDataPort = secondaryServerDataPort;
        this.mainServerIp = mainServerIp;
        this.secondaryServerIp = secondaryServerIp;
    }

    // endregion Constructors

    // region Public methods

    /**
     * To string method.
     * @return the string to print the config.
     */
    @Override
    public String toString() {
        return new StringJoiner("| ", ClientStateConfig.class.getSimpleName() + "[\n", "]")
                .add("mainServerCmdPort=" + mainServerCmdPort + "\n")
                .add("mainServerDataPort=" + mainServerDataPort + "\n")
                .add("secondaryServerCmdPort=" + secondaryServerCmdPort + "\n")
                .add("secondaryServerDataPort=" + secondaryServerDataPort + "\n")
                .add("mainServerIp='" + mainServerIp + "'" + "\n")
                .add("secondaryServerIp='" + secondaryServerIp + "'" + "\n")
                .add("isMainServerDown=" + isMainServerDown + "\n")
                .add("isMainServerConfigured=" + isMainServerConfigured + "\n")
                .add("isSecondaryServerConfigured=" + isSecondaryServerConfigured + "\n")
                .add("isSecondaryServerDown=" + isSecondaryServerDown + "\n")
                .add("isServerConnected=" + isServerConnected() + "\n")
                .toString();
    }

    // region Getters and Setters.

    public int getMainServerCmdPort() {
        return mainServerCmdPort;
    }

    public void setMainServerCmdPort(int mainServerCmdPort) {
        this.mainServerCmdPort = mainServerCmdPort;
    }

    public int getMainServerDataPort() {
        return mainServerDataPort;
    }

    public void setMainServerDataPort(int mainServerDataPort) {
        this.mainServerDataPort = mainServerDataPort;
    }

    public int getSecondaryServerCmdPort() {
        return secondaryServerCmdPort;
    }

    public void setSecondaryServerCmdPort(int secondaryServerCmdPort) {
        this.secondaryServerCmdPort = secondaryServerCmdPort;
    }

    public int getSecondaryServerDataPort() {
        return secondaryServerDataPort;
    }

    public void setSecondaryServerDataPort(int secondaryServerDataPort) {
        this.secondaryServerDataPort = secondaryServerDataPort;
    }

    public String getMainServerIp() {
        return mainServerIp;
    }

    public void setMainServerIp(String mainServerIp) {
        this.mainServerIp = mainServerIp;
    }

    public String getSecondaryServerIp() {
        return secondaryServerIp;
    }

    public void setSecondaryServerIp(String secondaryServerIp) {
        this.secondaryServerIp = secondaryServerIp;
    }

    public boolean isMainServerDown() {
        return isMainServerDown;
    }

    public void setMainServerDown(boolean mainServerDown) {
        isMainServerDown = mainServerDown;
    }

    public boolean isMainServerConfigured() {
        return isMainServerConfigured;
    }

    public void setMainServerConfigured(boolean mainServerConfigured) {
        isMainServerConfigured = mainServerConfigured;
    }

    public boolean isSecondaryServerConfigured() {
        return isSecondaryServerConfigured;
    }

    public void setSecondaryServerConfigured(boolean secondaryServerConfigured) {
        isSecondaryServerConfigured = secondaryServerConfigured;
    }

    public boolean isSecondaryServerDown() {
        return isSecondaryServerDown;
    }

    public void setSecondaryServerDown(boolean secondaryServerDown) {
        isSecondaryServerDown = secondaryServerDown;
    }

    public boolean isServerConnected() {
        return isServerConnected;
    }

    public void setServerConnected(boolean serverConnected) {
        isServerConnected = serverConnected;
    }

    public void switchServerConfig() {
        this.mainServerIp = secondaryServerIp;
        this.secondaryServerIp = mainServerIp;
        this.mainServerCmdPort = secondaryServerCmdPort;
        this.mainServerDataPort = secondaryServerDataPort;
        this.secondaryServerCmdPort = mainServerCmdPort;
        this.secondaryServerDataPort = mainServerDataPort;
    }

    // endregion Getters and Setters.

    // endregion Public methods
}
