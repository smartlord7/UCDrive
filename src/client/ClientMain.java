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

package client;

import client.struct.ClientStateConfig;
import com.google.gson.Gson;
import datalayer.model.SessionLog.SessionLog;
import datalayer.model.User.User;
import client.struct.ClientUserSession;
import protocol.clientserver.Request;
import protocol.clientserver.RequestMethodEnum;
import protocol.clientserver.Response;
import protocol.clientserver.ResponseStatusEnum;
import util.FileMetadata;
import util.FileUtil;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import util.Const;
import util.StringUtil;
import static sun.nio.ch.IOStatus.EOF;

/**
 * Class that has the client main methods.
 */
public class ClientMain {

    // region Private properties

    private String currLocalDir = System.getProperty("user.dir");
    private ObjectOutputStream outCmd;
    private ObjectInputStream inCmd;
    private DataOutputStream outData;
    private DataInputStream inData;
    private Socket cmdSocket;
    private Socket dataSocket;
    private HashMap<String, String> errors;
    private String line;
    private StringTokenizer st;
    private ClientUserSession session = null;
    private Response resp = new Response();
    private final ClientStateConfig config = new ClientStateConfig();
    private final User user = new User();
    private final SessionLog sessionLog = new SessionLog();
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final Request req = new Request();
    private final Gson gson = new Gson();

    // endregion Private properties

    // region Private methods

    /**
     * Method to print the response errors.
     * @param errors the occurred errors.
     */
    private void showResponseErrors(HashMap<String, String> errors) {
        for (String key : errors.keySet()) {
            System.out.println(key + ": " + errors.get(key));
        }
    }

    /**
     * Method that prints the error message.
     * @param msg is the error message.
     */
    private void error(String msg) {
        System.out.println(Const.COLOR_RED + "Error: " + msg + "." + Const.COLOR_RESET);
    }

    /**
     * Method that shows a bash like display.
     * @param user the logged user.
     * @param currLocalDir the current local directory.
     * @return the cmd prefix.
     */
    private String cmdPrefix(User user, String currLocalDir) {
        String userName;
        String prefix;

        userName = user.getUserName();
        prefix = (config.isServerConnected() ? "|Connected to " + cmdSocket.getInetAddress().getHostAddress() + "|\n" : "");
        prefix += (user.isAuth() ? userName + "@" : "") + Const.APP_NAME + "-local~\\" + currLocalDir + "\n";

        if (user.isAuth()) {
            prefix = Const.COLOR_YELLOW + prefix + StringUtil.repeat(" ", userName.length()) + "@"
                    + Const.APP_NAME + "-remote~\\" + session.getCurrentDir() + Const.COLOR_RESET + "\n" + Const.CMD_SYMBOL;
        } else {
            prefix = Const.COLOR_YELLOW + prefix + Const.COLOR_RESET + Const.CMD_SYMBOL;
        }

        return prefix;
    }

    /**
     * Method that checks if user is authenticated.
     * @return true if user is authenticated or false if user isn't authenticated.
     */
    private boolean hasAuth() {
        if (!user.isAuth()) {
            error("user not logged in");

            return false;
        }

        return true;
    }

    /**
     * Method that checks if the server is connected.
     * @return true if the server is connected or false if the server isn't connected.
     */
    private boolean hasConnection() {
        if (!config.isServerConnected()) {
            error("server not connected");

            return false;
        }

        return true;
    }

    /**
     * Method that checks if there is a session (Connection + Authentication)
     * @return a flag if there is a session.
     */
    private boolean hasSession() {
        return hasConnection() && hasAuth() && session != null;
    }

    /**
     * Method that switches to the secondary server.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void switchToSecondaryServer() throws IOException {
        if (config.isMainServerDown()) {
            error("main server is down");

            if (config.isSecondaryServerConfigured()) {
                System.out.println("Switching to secondary server...");
                connectServer_(config.getSecondaryServerIp(), config.getSecondaryServerCmdPort(), config.getSecondaryServerDataPort());

                if (config.isServerConnected()) {
                    config.switchServerConfig();
                    config.setMainServerDown(false);
                }
            } else {
                error("can't switch to secondary server since it is not configured. After configuring it, you have to connect manually to it");
                config.setServerConnected(false);
                user.setAuth(false);
            }
        } else {
            error("secondary server down. Nothing more you can do");
            config.setServerConnected(false);
            user.setAuth(false);
        }
    }

    /**
     * Method that exchanges the request and the response.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void exchangeReqResp() throws IOException, ClassNotFoundException {
        resp.setValid(false);
        sendRequest();

        if (config.isServerConnected()) {
            receiveResponse();

            if (config.isServerConnected()) {
                resp.setValid(true);
            }
        }
    }

    /**
     * Method that sends the requests to the server.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void sendRequest() throws IOException {
        try {
            req.setSession(session);
            outCmd.writeObject(req);
        } catch (SocketException e) {
            if (!config.isMainServerDown()) {
                config.setMainServerDown(true);
                switchToSecondaryServer();

                if (config.isServerConnected()) {
                    outCmd.writeObject(req);
                }
            } else {
                error("secondary server down. Nothing more you can do");
                config.setServerConnected(false);
                user.setAuth(false);
            }
        } catch (IOException e) {
            error("could not send request");
        }
    }

    /**
     * Method that sends the data in a byte array to the server.
     * @param data is the data to be sent.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private int sendData(byte[] data) throws IOException {
        try {
            outData.write(data);
            outData.flush();
        } catch (SocketException e) {
            if (!config.isMainServerDown()) {
                config.setMainServerDown(true);
                switchToSecondaryServer();

                if (config.isServerConnected()) {
                    return -2;
                }
            } else {
                error("secondary server down. Nothing more you can do");
                config.setServerConnected(false);
                user.setAuth(false);
            }
        } catch (IOException e) {
            error("could not send data");

            if (config.isServerConnected()) {
                return -2;
            }
        }

        return EOF;
    }

    /**
     * Method that receives the server response to the request.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void receiveResponse() throws IOException, ClassNotFoundException {
        try {
           resp = (Response) inCmd.readObject();
        } catch (SocketException e) {
            if (!config.isMainServerDown()) {
                config.setMainServerDown(true);
                switchToSecondaryServer();

            } else {
                error("secondary server down. Nothing more you can do");
                config.setServerConnected(false);
                user.setAuth(false);
            }
        } catch (IOException e) {
            error("could not receive response");
        }
    }

    /**
     * Method that receives the data.
     * @param buffer is the buffer containing the data.
     * @param readSize is the buffer read size.
     * @return the number of read bytes or a code for further validations.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private int receiveData(byte[] buffer, int readSize) throws IOException {
        try {
            return inData.read(buffer, 0, readSize);
        } catch (SocketException e) {
            if (!config.isMainServerDown()) {
                config.setMainServerDown(true);
                switchToSecondaryServer();

                if (config.isServerConnected()) {
                    return -2;
                }
            } else {
                error("secondary server down. Nothing more you can do");
                config.setServerConnected(false);
                user.setAuth(false);
            }
        } catch (IOException e) {
            error("could not receive response");

            if (config.isServerConnected()) {
                return -2;
            }
        }

        return EOF;
    }

    /**
     * Method used to connect to the server.
     * @param ip is the server ip
     * @param cmdPort is the command handler port.
     * @param dataPort is the data handler port.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void connectServer_(String ip, int cmdPort, int dataPort) throws IOException {
        config.setServerConnected(false);
        try {
            cmdSocket = new Socket(ip, cmdPort);
        } catch (SocketException | UnknownHostException e) {
            error("cmd host " + ip + ":" + cmdPort + " unreachable");
            return;
        }

        System.out.println("Command channel connected to " + ip + ":" + cmdPort);

        try {
            dataSocket = new Socket(ip, dataPort);
        } catch (SocketException | UnknownHostException e) {
            error("data host " + ip + ":" + dataPort + " unreachable");
            cmdSocket.close();
            return;
        }

        System.out.println("Data channel connected to " + ip + ":" + dataPort);

        outCmd = new ObjectOutputStream(new DataOutputStream(cmdSocket.getOutputStream()));
        inCmd = new ObjectInputStream(new DataInputStream(cmdSocket.getInputStream()));
        outData = new DataOutputStream(dataSocket.getOutputStream());
        inData = new DataInputStream(dataSocket.getInputStream());

        config.setServerConnected(true);
    }

    /**
     * Method used to connect to the server and if down,tries to connect to the secondary.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void connectServer() throws IOException {
        if (!config.isMainServerConfigured()) {
            error("main server not configured");
            return;
        }

        connectServer_(config.getMainServerIp(), config.getMainServerCmdPort(), config.getMainServerDataPort());

        if (!config.isServerConnected()) {
            error("main server is down");
            System.out.println("Switching to secondary server...");

            if (!config.isSecondaryServerConfigured()) {
                error("can't switch to secondary server since it is not configured. After configuring it, you have to connect manually to it");
                return;
            }

            connectServer_(config.getSecondaryServerIp(), config.getSecondaryServerCmdPort(), config.getSecondaryServerCmdPort());

            if (!config.isServerConnected()) {
                error("secondary server down. Nothing more you can do");
                return;
            }

            config.switchServerConfig();
            config.setMainServerDown(false);
        }
    }

    /**
     * Method used to disconnect from the server.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void disconnectServer() throws IOException {
        if (!hasConnection()) {
            return;
        }

        String answer;

        System.out.println("Are you sure you want to disconnect? (Y/N).");
        answer = in.readLine();

        if (answer.equalsIgnoreCase("y")) {
            user.setAuth(false);
            config.setServerConnected(false);
            clean();
        }
    }

    /**
     * Method used to configure the servers.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void configServers() throws IOException {
        String selectedServer;
        String line;

        try {
            selectedServer = st.nextToken();
        } catch (NoSuchElementException e) {
            error("Error: malformed command");
            return;
        }

        try {
            if (selectedServer.equalsIgnoreCase("m")) {
                System.out.print("IP address: ");
                line = in.readLine();
                if (StringUtil.isEmptyOrNull(line)) {
                    return;
                }
                config.setMainServerIp(line);
                System.out.print("Command channel port: ");
                config.setMainServerCmdPort(Integer.parseInt(in.readLine()));
                System.out.print("Data channel port: ");
                config.setMainServerDataPort(Integer.parseInt(in.readLine()));
                config.setMainServerConfigured(true);
            } else {
                System.out.print("IP address: ");
                line = in.readLine();
                if (StringUtil.isEmptyOrNull(line)) {
                    return;
                }
                config.setSecondaryServerIp(line);
                System.out.print("Command channel port: ");
                config.setSecondaryServerCmdPort(Integer.parseInt(in.readLine()));
                System.out.print("Data channel port: ");
                config.setSecondaryServerDataPort(Integer.parseInt(in.readLine()));
                config.setSecondaryServerConfigured(true);
            }
        } catch (NumberFormatException ignored) {

        }
    }

    /**
     * Method used to authenticate the user.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void authUser() throws IOException, ClassNotFoundException {
        if (user.isAuth()) {
            error("user already logged in");
            return;
        }

        if (!hasConnection()) {
            return;
        }

        System.out.print("Username: ");
        user.setUserName(in.readLine());
        System.out.print("Password: ");
        user.setPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_AUTHENTICATION);
        req.setContent(gson.toJson(user));

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                System.out.println("User '" + user.getUserName() + "' authenticated successfully.");
                session = gson.fromJson(resp.getContent(), ClientUserSession.class);
                user.setAuth(true);
                sessionLog.setStartDate(new Timestamp(System.currentTimeMillis()));
            } else {
                errors = resp.getErrors();
                showResponseErrors(errors);
            }
        }
    }

    /**
     * Method used to logout the user.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void logoutUser() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        String answer;

        System.out.println("Are you sure you want to logout? (Y/N)");
        answer = in.readLine();

        if (answer.equalsIgnoreCase("n")) {
            return;
        }

        req.setMethod(RequestMethodEnum.USER_LOGOUT);
        sessionLog.setEndDate(new Timestamp(System.currentTimeMillis()));
        req.setContent(gson.toJson(sessionLog));

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                System.out.println("User logged out.");
                user.setAuth(false);
            } else {
                showResponseErrors(resp.getErrors());
            }
        }
    }

    /**
     * Method used to register the user.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void registerUser() throws IOException, ClassNotFoundException {
        if (!hasConnection()) {
            return;
        }

        System.out.print("Username: ");
        user.setUserName(in.readLine());
        System.out.print("Password: ");
        user.setPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_CREATE);
        req.setContent(gson.toJson(user));

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                System.out.println("User '" + user.getUserName() + "' created successfully!");
                session = gson.fromJson(resp.getContent(), ClientUserSession.class);
                user.setAuth(true);
            } else {
                errors = resp.getErrors();
                showResponseErrors(errors);
            }
        }
    }

    /**
     * Method used to change the user password.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void changeUserPassword() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }
        System.out.print("Old password: ");
        user.setPassword(in.readLine());
        System.out.print("New password: ");
        user.setNewPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_CHANGE_PASSWORD);
        req.setContent(gson.toJson(user));

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                System.out.println("Password changed successfully!");
            } else {
                errors = resp.getErrors();
                showResponseErrors(errors);
            }
        }
    }

    /**
     * Method used to list the local directory.
     */
    private void listLocalDir() {
        String dir = currLocalDir;

        if (st.hasMoreTokens()) {
            dir = st.nextToken();
            if ((dir = FileUtil.parseDir(line, dir)) == null) {
                return;
            }
            dir = currLocalDir + "\\" + dir;
        }


        File file = new File(dir);

        if (!file.isDirectory() || !file.exists()) {
            error("no directory '" + dir + "' found");
        } else {
            System.out.println(FileUtil.listDirFiles(file));
        }
    }

    /**
     * Method used to list the remote directory.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void listRemoteDir() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        String dir = null;

        if (st.hasMoreTokens()) {
            dir = st.nextToken();
            if ((dir = FileUtil.parseDir(line, dir)) == null) {
                return;
            }
        }

        req.setMethod(RequestMethodEnum.USER_LIST_SERVER_FILES);
        req.setContent(dir);

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                System.out.println(resp.getContent());
            } else {
                errors = resp.getErrors();
                showResponseErrors(errors);
            }
        }
    }

    /**
     * Method that changes the local current working directory.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void changeLocalCWD() throws IOException {
        String dir;

        try {
            dir = st.nextToken();
        } catch (NoSuchElementException e) {
            error("missing argument");
            return;
        }

        if ((dir = FileUtil.parseDir(line, dir)) == null) {
            return;
        }

        currLocalDir = FileUtil.getNextCWD(dir, currLocalDir);
    }

    /**
     * Method that changes the remote current working directory.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void changeRemoteCWD() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        String dir;

        try {
            dir = st.nextToken();
        } catch (NoSuchElementException e) {
            error("missing argument");
            return;
        }

        if ((dir = FileUtil.parseDir(line, dir)) == null) {
            return;
        }

        req.setMethod(RequestMethodEnum.USER_CHANGE_CWD);
        req.setContent(dir);

        exchangeReqResp();

        if (resp.isValid()) {
            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                session.setCurrentDir(resp.getContent());
            } else {
                errors = resp.getErrors();
                showResponseErrors(errors);
            }
        }
    }

    /**
     * Method to upload the files.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void uploadFiles() throws IOException, ClassNotFoundException {
        String fileName;
        String[] split;
        DataInputStream fileReader;

        if (!hasSession()) {
            return;
        }

        if (!st.hasMoreTokens()) {
            error("missing argument");
            return;
        }

        split = line.split("'");

        if (split.length == 1) {
            split = line.split("\\s+");
        }

        for (int i = 1; i < split.length; i++) {
            fileName = split[i];

            if (fileName.strip().length() == 0) {
                continue;
            }

            Path path = Paths.get(fileName);

            if (!Files.exists(path)) {

                fileName = currLocalDir + "\\" + fileName;
                path = Paths.get(fileName);

                if (!Files.exists(path)) {
                    error("file '" + fileName + "' does not exist");
                    return;
                }
            }

            if (Files.size(path) == 0) {
                error("file '" + fileName + "' is empty");
                return;
            }

            Path p = Paths.get(fileName);
            FileMetadata info = new FileMetadata(p.getName(p.getNameCount() - 1).toString(), (int) Files.size(Paths.get(fileName)));
            req.setMethod(RequestMethodEnum.USER_UPLOAD_FILE);
            req.setSession(session);
            req.setContent(gson.toJson(info));

            int counter = 1;
            while (true) {

                exchangeReqResp();

                if (resp.isValid()) {
                    if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                        fileReader = new DataInputStream(new FileInputStream(fileName));
                        boolean retry = false;
                        int fileSize = info.getFileSize();
                        byte[] buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];

                        int readSize = Math.min(fileSize, Const.UPLOAD_FILE_CHUNK_SIZE);

                        while (fileReader.read(buffer, 0, readSize) != -1) {
                            if (sendData(buffer) == -2) {
                                retry = true;
                                break;
                            }
                        }

                        fileReader.close();

                        if (!retry) {
                            System.out.println("File '" + fileName + "' successfully uploaded. ");
                            break;
                        } else {
                            if (!config.isServerConnected()) {
                                error("could not upload " + fileName);
                                return;
                            }
                            error("could not upload " + fileName + ". Attempting retry no " + counter);
                            counter++;
                        }
                    } else if (resp.getStatus() == ResponseStatusEnum.UNAUTHORIZED) {
                        showResponseErrors(resp.getErrors());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method to download the files.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    private void downloadFiles() throws IOException, ClassNotFoundException {
        String fileName;
        String[] split;

        if (!hasSession()) {
            return;
        }

        if (!st.hasMoreTokens()) {
            error("missing argument");
            return;
        }

        FileMetadata fileMeta;
        split = line.split("'");

        if (split.length == 1) {
            split = line.split("\\s+");
        }

        for (int i = 1; i < split.length; i++) {
            fileName = split[i];

            if (fileName.strip().length() == 0) {
                continue;
            }

            int counter = 1;
            req.setMethod(RequestMethodEnum.USER_DOWNLOAD_FILE);
            fileMeta = new FileMetadata();
            fileMeta.setFileName(st.nextToken());
            req.setContent(gson.toJson(fileMeta));
            req.setSession(session);

            while (true) {

                exchangeReqResp();

                if (resp.isValid()) {
                    if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                        fileMeta = gson.fromJson(resp.getContent(), FileMetadata.class);
                        FileMetadata finalFileMeta = fileMeta;

                        int totalRead = 0;
                        int bytesRead;
                        int readSize;
                        FileOutputStream fileWriter;

                        int fileSize = finalFileMeta.getFileSize();
                        readSize = Math.min(fileSize, Const.DOWNLOAD_FILE_CHUNK_SIZE);
                        byte[] buffer = new byte[readSize];
                        fileWriter = new FileOutputStream(currLocalDir + "\\" + finalFileMeta.getFileName());

                        while ((bytesRead = receiveData(buffer, readSize)) > 0) {
                            byte[] finalBuffer = buffer;

                            if (bytesRead > finalFileMeta.getFileSize()) {
                                finalBuffer = FileUtil.substring(buffer, 0, fileSize);
                            }

                            totalRead += bytesRead;
                            fileWriter.write(finalBuffer);

                            if (totalRead >= fileSize) {
                                fileWriter.close();
                                System.out.println("File '" + finalFileMeta.getFileName() + "' successfully downloaded.\n");
                                break;
                            }
                        }

                        fileWriter.close();
                        if (bytesRead != -2) {
                            break;
                        } else {
                            if (!config.isServerConnected()) {
                                error("could not upload " + fileName);
                                return;
                            }

                            error("could not download " + fileMeta.getFileName() + ". Attempting retry no " + counter);
                            counter++;
                        }
                    } else {
                        showResponseErrors(resp.getErrors());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method to print the config.
     */
    private void showConfig() {
        System.out.println(config);
    }

    /**
     * Method that prints the help menu.
     */
    private void showHelp() {
        System.out.println("""
                ----------------------------------------------------------------------------------------------------------------
                Help:
                \tconfig <server> - enter server configuration mode: m for main server and other for secondary server
                \tshowconfig - show current client state/config (i.e. servers IPs, ports, ...)
                \tconnect - connect to the server
                \tdisconnect - disconnect to the server
                \tauth - enter authentication mode
                \tlogout - logout from server
                \tregister - enter registration mode
                \tcpwd - change user password
                \tls <dir> - list <dir> local files or the current local directory files, if <dir> is not specified
                \tsls <dir> - list <dir> remote files or the current remote directory files, if <dir> is not specified
                \tcd <dir> - change the current local directory to <dir>
                \tcd <dir> - change the current remote directory to <dir>
                \tupload <filepath> - upload the current local directory file specified by <path> to the current remote directory
                \tdownload <filepath> - download the current remote directory file specified by <path> to the current local directory
                ----------------------------------------------------------------------------------------------------------------""");
    }

    /**
     * Method that prints the Menu.
     */
    private void showMenu() {
        System.out.println(Const.COLOR_BLUE + """
                |---------------------------------------------------------------------------------------|
                |    ||       ||  |||||||||   |||||||     ||||||||     ||   ||        ||   |||||||||    |
                |    ||       ||  ||          ||     ||   ||     ||    ||    ||      ||    ||           |
                |    ||       ||  ||          ||      ||  |||||||||    ||     ||    ||     |||||||||    |
                |    ||       ||  ||          ||     ||   ||  \\\\       ||      ||  ||      ||           |
                |    |||||||||||  |||||||||   ||||||||    ||     \\\\    ||        ||        |||||||||    |
                |---------------------------------------------------------------------------------------|
                |-----------------------------------------Client----------------------------------------|
                
                
                @By Sancho Simões
                @By Tiago Ventura
                Bachelor in Computer Science and Engineering, University of Coimbra
                2021/2022 - 3rd year, 2nd semester - Distributed Systems
                
                Type '?' or 'help' to view the available commands
                """ + Const.COLOR_RESET);
    }

    /**
     * Method that clears the channels.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void clearChannels() throws IOException {
        if (config.isServerConnected()) {
            outCmd.flush();
            try {
                outCmd.reset();
            } catch (SocketException ignored) {

            }
            outData.flush();
        }
    }

    /**
     * Method used to close the threads.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void clean() throws IOException {
        if (config.isMainServerConfigured() || config.isSecondaryServerConfigured()) {
            inCmd.close();
            inData.close();
            outCmd.close();
            outData.close();
            cmdSocket.close();
            dataSocket.close();
        }
    }

    /**
     * Main method to run the choosen method by the client in the cmd.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */

    // endregion Private methods

    private void run() throws IOException, ClassNotFoundException {
        String cmd;

        showMenu();

        System.out.print(cmdPrefix(user, currLocalDir));
        while (!(line = in.readLine()).equalsIgnoreCase("exit")) {
            line = line.strip().trim();
            st = new StringTokenizer(line);

            if (!st.hasMoreTokens()) {
                System.out.print(Const.CMD_SYMBOL);
                continue;
            }
            cmd = st.nextToken();

            if (cmd.equalsIgnoreCase("help") || cmd.equals("?")) {
                showHelp();
            } else if (cmd.equalsIgnoreCase("config")) {
                configServers();
            } else if (cmd.equalsIgnoreCase("showconfig")) {
                showConfig();
            } else if (cmd.equalsIgnoreCase("connect")){
                connectServer();
            } else if (cmd.equalsIgnoreCase("disconnect")){
                disconnectServer();
            }else if (cmd.equalsIgnoreCase("auth")) {
                authUser();
            } else if (cmd.equalsIgnoreCase("logout")) {
                logoutUser();
            } else if (cmd.equalsIgnoreCase("register")) {
                registerUser();
            } else if (line.equalsIgnoreCase("cpwd")) {
                changeUserPassword();
            } else if (cmd.equalsIgnoreCase("ls")) {
                listLocalDir();
            } else if (cmd.equalsIgnoreCase("sls")) {
                listRemoteDir();
            } else if (cmd.equalsIgnoreCase("cd")) {
                changeLocalCWD();
            } else if (cmd.equalsIgnoreCase("scd")) {
                changeRemoteCWD();
            } else if (cmd.equalsIgnoreCase("upload")) {
                uploadFiles();
            } else if (cmd.equalsIgnoreCase("download")) {
                downloadFiles();
            } else {
                error("unknown command '" + line + "'");
            }

            clearChannels();
            System.out.print(cmdPrefix(user, currLocalDir));
        }

        clean();
        System.exit(0);
    }

    // region Constructors

    /**
     * Main method.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    public ClientMain() throws IOException, ClassNotFoundException {
        run();
    }

    // endregion Constructors

    // region Public methods

    /**
     * Main method.
     * @param args are the main arguments.
     * @throws IOException - whenever an input or output operation is failed or interrupted.
     * @throws ClassNotFoundException - when the Java Virtual Machine (JVM) tries to load a particular class and the specified class cannot be found in the classpath.
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new ClientMain();
    }

    // endregion Public methods

}
