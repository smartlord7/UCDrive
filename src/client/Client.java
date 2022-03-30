package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import datalayer.model.User.UserSession;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;
import protocol.ResponseStatusEnum;
import util.FileMetadata;
import util.FileUtil;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import util.Const;
import util.StringUtil;

public class Client {
    private boolean serverConfigured = false;
    private String currLocalDir = System.getProperty("user.dir");
    private ObjectOutputStream outCmd;
    private ObjectInputStream inCmd;
    private ObjectOutputStream outData;
    private ObjectInputStream inData;
    private Response resp;
    private UserSession session = null;
    private Socket cmdSocket;
    private Socket dataSocket;
    private HashMap<String, String> errors;
    private StringTokenizer st;
    private final User user = new User();
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final Request req = new Request();
    private final Gson gson = new Gson();

    private void showErrors(HashMap<String, String> errors) {
        for (String key : errors.keySet()) {
            System.out.println(key + ": " + errors.get(key));
        }
    }

    private String cmdPrefix(User user, String currLocalDir) {
        String userName;
        String prefix;

        userName = user.getUserName();
        prefix = (user.isAuth() ? userName + "@" : "") + Const.APP_NAME + "-local~\\" + currLocalDir + "\n";

        if (user.isAuth()) {
            prefix += StringUtil.repeat(" ", userName.length()) + "@" + Const.APP_NAME + "-remote~\\" + session.getCurrentDir() + "\n$ ";
        } else {
            prefix += "$ ";
        }

        return prefix;
    }

    private boolean hasAuth() {
        if (!user.isAuth()) {
            System.out.println("Error: user not logged in!");
            return false;
        }

        return true;
    }

    private boolean hasConnection() {
        if (!serverConfigured) {
            System.out.println("Error: server not configured!");
            return false;
        }

        return true;
    }

    private boolean hasSession() {
        return hasConnection() && hasAuth();
    }

    private void configServer() throws IOException {
        String ip = "";
        int cmdPort;
        int dataPort;

        while (!serverConfigured) {
                System.out.println("Server IP: ");
                ip = in.readLine();
                System.out.println("Server cmd port: ");
                cmdPort = Integer.parseInt(in.readLine());

                try {

                    cmdSocket = new Socket(ip, cmdPort);
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("Error: cmd host " + ip + ":" + cmdPort + " unreachable");
                    continue;
                }

                System.out.println("Connected to " + ip + ":" + cmdPort);

                System.out.println("Server data port: ");
                dataPort = Integer.parseInt(in.readLine());

                try {
                    dataSocket = new Socket(ip, dataPort);
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("Error: data host " + ip + ":" + dataPort + " unreachable");
                    continue;
                }

                System.out.println("Connected to " + ip + ":" + dataPort);

                serverConfigured = true;
        }

        outCmd = new ObjectOutputStream(new DataOutputStream(cmdSocket.getOutputStream()));
        inCmd = new ObjectInputStream(new DataInputStream(cmdSocket.getInputStream()));
        outData = new ObjectOutputStream(new DataOutputStream(dataSocket.getOutputStream()));
        inData = new ObjectInputStream(new DataInputStream(dataSocket.getInputStream()));
    }

    private void authUser() throws IOException, ClassNotFoundException {
        if (user.isAuth()) {
            System.out.println("Error: user already logged in!");
            return;
        }

        if (!hasConnection()) {
            return;
        }

        System.out.println("Username: ");
        user.setUserName(in.readLine());
        System.out.println("Password: ");
        user.setPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_AUTHENTICATION);
        req.setData(gson.toJson(user));
        outCmd.writeObject(req);

        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            System.out.println("User '" + user.getUserName() + "' authenticated successfully!");
            session = gson.fromJson(resp.getData(), UserSession.class);
            user.setAuth(true);
        } else {
            errors = resp.getErrors();
            showErrors(errors);
        }
    }

    private void changeUserPassword() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }
        System.out.println("Old password: ");
        user.setPassword(in.readLine());
        System.out.println("New password: ");
        user.setNewPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_CHANGE_PASSWORD);
        req.setData(gson.toJson(user));
        outCmd.writeObject(req);

        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            System.out.println("Password changed successfully!");
        } else {
            errors = resp.getErrors();
            showErrors(errors);
        }
    }

    private void listLocalDir() {
        String dir = currLocalDir;

        if (st.hasMoreTokens()) {
            dir = st.nextToken();
        }

        File file = new File(dir);

        if (!file.isDirectory() || !file.exists()) {
            System.out.println("Error: no directory '" + dir + "' found!");
        } else {
            System.out.println(FileUtil.listDirFiles(file));
        }
    }

    private void listRemoteDir() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        String targetDir = null;

        if (st.hasMoreTokens()) {
            targetDir = st.nextToken();
        }

        req.setMethod(RequestMethodEnum.USER_LIST_SERVER_FILES);
        req.setData(targetDir);
        outCmd.writeObject(req);

        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            System.out.println(resp.getData());
        } else {
            errors = resp.getErrors();
            showErrors(errors);
        }
    }

    private void changeLocalCWD() throws IOException {
        String targetDir = st.nextToken();
        currLocalDir = FileUtil.getNextCWD(targetDir, currLocalDir);
    }

    private void changeRemoteCWD() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        String targetDir = st.nextToken();
        req.setMethod(RequestMethodEnum.USER_CHANGE_CWD);
        req.setData(targetDir);
        outCmd.writeObject(req);
        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            session.setCurrentDir(resp.getData());
        } else {
            errors = resp.getErrors();
            showErrors(errors);
        }
    }

    private void uploadFiles() throws IOException, ClassNotFoundException {
        ArrayList<String> filesToUpload;
        DataInputStream fileReader;

        if (!hasSession()) {
            return;
        }

        filesToUpload = new ArrayList<>();

        while (st.hasMoreTokens()) {
            String fileName = st.nextToken();
            Path path = Paths.get(fileName);

            if (!Files.exists(path)) {

                fileName = currLocalDir + "\\" + fileName;
                path = Paths.get(fileName);

                if (!Files.exists(path)) {
                    System.out.println("Error: file " + fileName + " does not exist!");
                    return;
                }
            }

            if (Files.size(path) == 0) {
                System.out.println("Error: file " + fileName + " is empty!");
                return;
            }

            filesToUpload.add(fileName);
        }

        for (String file : filesToUpload) {
            Path p = Paths.get(file);
            FileMetadata info = new FileMetadata(p.getName(p.getNameCount() - 1).toString(), (int) Files.size(Paths.get(file)));
            req.setMethod(RequestMethodEnum.USER_UPLOAD_FILES);
            req.setData(gson.toJson(info));
            outCmd.writeObject(req);

            resp = (Response) inCmd.readObject();

            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                fileReader = new DataInputStream(new FileInputStream(file));
                int fileSize = info.getFileSize();
                byte[] buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];

                int readSize = Math.min(fileSize, Const.UPLOAD_FILE_CHUNK_SIZE);

                while (fileReader.read(buffer, 0, readSize) != -1) {
                    outData.write(buffer);
                }
            } else if (resp.getStatus() == ResponseStatusEnum.UNAUTHORIZED){
                showErrors(resp.getErrors());
            }
        }
    }

    private void downloadFiles() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        req.setMethod(RequestMethodEnum.USER_DOWNLOAD_FILES);
        outCmd.writeObject(req);
        resp = (Response) inCmd.readObject();
    }

    private void clearChannels() throws IOException {
        if (serverConfigured) {
            outCmd.flush();
            outCmd.reset();
            outData.flush();
            outData.reset();
        }
    }

    private void cleanAndExit() throws IOException {
        in.close();
        inCmd.close();
        inData.close();
        outCmd.close();
        outData.close();
        cmdSocket.close();
        dataSocket.close();
        System.exit(0);
    }

    public void run() throws IOException, ClassNotFoundException {
        String line;
        configServer();

        System.out.print(cmdPrefix(user, currLocalDir));
        while (!(line = in.readLine()).equalsIgnoreCase("exit")) {
            line = line.strip().trim();
            st = new StringTokenizer(line);
            String cmd = st.nextToken();
            if (cmd.equalsIgnoreCase("auth")) {
                authUser();
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
                System.out.println("Unknown command '" + line + "'");
            }

            clearChannels();
            System.out.print(cmdPrefix(user, currLocalDir));
        }

        cleanAndExit();
    }

    public Client() throws IOException, ClassNotFoundException {
        run();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
    }
}
