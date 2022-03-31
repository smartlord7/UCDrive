package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import datalayer.model.User.ClientUserSession;
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

import static sun.nio.ch.IOStatus.EOF;

public class ClientMain {
    private boolean serverConfigured = false;
    private String currLocalDir = System.getProperty("user.dir");
    private ObjectOutputStream outCmd;
    private ObjectInputStream inCmd;
    private DataOutputStream outData;
    private DataInputStream inData;
    private Response resp;
    private ClientUserSession session = null;
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

    private void configServer(String ip, int cmdPort, int dataPort) throws IOException {
        try {

            cmdSocket = new Socket(ip, cmdPort);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Error: cmd host " + ip + ":" + cmdPort + " unreachable");
            System.exit(-1);
        }

        System.out.println("Command channel connected to " + ip + ":" + cmdPort);


        try {
            dataSocket = new Socket(ip, dataPort);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Error: data host " + ip + ":" + dataPort + " unreachable");
            System.exit(-1);
        }

        System.out.println("Data channel connected to " + ip + ":" + dataPort);

        serverConfigured = true;

        outCmd = new ObjectOutputStream(new DataOutputStream(cmdSocket.getOutputStream()));
        inCmd = new ObjectInputStream(new DataInputStream(cmdSocket.getInputStream()));
        outData = new DataOutputStream(dataSocket.getOutputStream());
        inData = new DataInputStream(dataSocket.getInputStream());
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
            session = gson.fromJson(resp.getData(), ClientUserSession.class);
            user.setAuth(true);
        } else {
            errors = resp.getErrors();
            showErrors(errors);
        }
    }

    private void logoutUser() throws IOException, ClassNotFoundException {
        String answer;

        System.out.println("Are you sure you want to logout? (Y/N)");
        answer = in.readLine();

        if (answer.equalsIgnoreCase("n")) {
            return;
        }

        req.setMethod(RequestMethodEnum.USER_LOGOUT);
        outCmd.writeObject(req);

        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            System.out.println("Logged out.");
        } else {
            showErrors(resp.getErrors());
        }
    }

    private void registerUser() throws IOException, ClassNotFoundException {
        if (!hasConnection()) {
            return;
        }

        System.out.println("Username: ");
        user.setUserName(in.readLine());
        System.out.println("Password: ");
        user.setPassword(in.readLine());

        req.setMethod(RequestMethodEnum.USER_CREATE);
        req.setData(gson.toJson(user));
        outCmd.writeObject(req);

        resp = (Response) inCmd.readObject();

        if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
            System.out.println("User '" + user.getUserName() + "' created successfully!");
            session = gson.fromJson(resp.getData(), ClientUserSession.class);
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
                    System.out.println("Error: file '" + fileName + "' does not exist!");
                    return;
                }
            }

            if (Files.size(path) == 0) {
                System.out.println("Error: file '" + fileName + "' is empty!");
                return;
            }

            filesToUpload.add(fileName);
        }

        for (String file : filesToUpload) {
            Path p = Paths.get(file);
            FileMetadata info = new FileMetadata(p.getName(p.getNameCount() - 1).toString(), (int) Files.size(Paths.get(file)));
            req.setMethod(RequestMethodEnum.USER_UPLOAD_FILE);
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
                    outData.flush();
                }

                fileReader.close();
            } else if (resp.getStatus() == ResponseStatusEnum.UNAUTHORIZED){
                showErrors(resp.getErrors());
            }
        }
    }

    private void downloadFiles() throws IOException, ClassNotFoundException {
        if (!hasSession()) {
            return;
        }

        FileMetadata fileMeta;

        while (st.hasMoreTokens()) {
            req.setMethod(RequestMethodEnum.USER_DOWNLOAD_FILE);
            fileMeta = new FileMetadata();
            fileMeta.setFileName(st.nextToken());
            req.setData(gson.toJson(fileMeta));
            outCmd.writeObject(req);
            resp = (Response) inCmd.readObject();

            if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                fileMeta = gson.fromJson(resp.getData(), FileMetadata.class);
               new Thread(new ClientDataWorker(inData, fileMeta, currLocalDir)).start();
            } else {
                showErrors(resp.getErrors());
            }
        }
    }

    private void clearChannels() throws IOException {
        if (serverConfigured) {
            outCmd.flush();
            outCmd.reset();
            outData.flush();
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

        System.out.print(cmdPrefix(user, currLocalDir));
        while (!(line = in.readLine()).equalsIgnoreCase("exit")) {
            line = line.strip().trim();
            st = new StringTokenizer(line);
            String cmd = st.nextToken();
            if (cmd.equalsIgnoreCase("auth")) {
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
                System.out.println("Unknown command '" + line + "'");
            }

            clearChannels();
            System.out.print(cmdPrefix(user, currLocalDir));
        }

        cleanAndExit();
    }

    public ClientMain(String ip, int commandPort, int dataPort) throws IOException, ClassNotFoundException {
        configServer(ip, commandPort, dataPort);
        run();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientMain client = new ClientMain(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}
