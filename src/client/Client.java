package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import datalayer.model.User.UserSession;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;
import protocol.ResponseStatusEnum;
import util.FileUtil;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.StringTokenizer;
import util.Const;
import util.StringUtil;

public class Client {
    private ObjectOutputStream out;
    private ObjectInputStream input;
    private String currLocalDir = System.getProperty("user.dir");
    private Response resp;
    private UserSession session = null;
    private boolean serverConfigured = false;
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

    public void run() throws IOException, ClassNotFoundException {
        String line;
        String ip = null;
        int port = -1;
        Response resp;
        Socket s = null;

        while (!serverConfigured) {
            try {
                System.out.println("Server IP: ");
                ip = in.readLine();
                System.out.println("Server port: ");
                port = Integer.parseInt(in.readLine());

                s = new Socket(ip, port);
                serverConfigured = true;
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Error: host " + ip + ":" + port + " unreachable");
            }
        }

        out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));
        input = new ObjectInputStream(new DataInputStream(s.getInputStream()));
        System.out.println("Connected to " + ip + ":" + port);
        serverConfigured = true;
        System.out.print(cmdPrefix(user, currLocalDir));
        while (!(line = in.readLine()).equalsIgnoreCase("exit")) {
            line = line.strip().trim();

            StringTokenizer st = new StringTokenizer(line);
            String cmd = st.nextToken();
            HashMap<String, String> errors;
            if (cmd.equalsIgnoreCase("auth")) {
                if (user.isAuth()) {
                    System.out.println("Error: user already logged in!");
                    continue;
                }

                if (!hasConnection()) {
                    continue;
                }

                System.out.println("Username: ");
                user.setUserName(in.readLine());
                System.out.println("Password: ");
                user.setPassword(in.readLine());

                req.setMethod(RequestMethodEnum.USER_AUTHENTICATION);
                req.setData(gson.toJson(user));
                out.writeObject(req);

                resp = (Response) input.readObject();

                if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                    System.out.println("User '" + user.getUserName() + "' authenticated successfully!");
                    session = gson.fromJson(resp.getData(), UserSession.class);
                    user.setAuth(true);
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }
            } else if (line.equalsIgnoreCase("cpwd")) {
                if (!hasSession()) {
                    continue;
                }

                System.out.println("Old password: ");
                user.setPassword(in.readLine());
                System.out.println("New password: ");
                user.setNewPassword(in.readLine());

                req.setMethod(RequestMethodEnum.USER_CHANGE_PASSWORD);
                req.setData(gson.toJson(user));
                out.writeObject(req);

                resp = (Response) input.readObject();

                if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                    System.out.println("Password changed successfully!");
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }

            } else if (cmd.equalsIgnoreCase("ls")) {
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
            } else if (cmd.equalsIgnoreCase("sls")) {
                if (!hasSession()) {
                    continue;
                }

                String targetDir = null;

                if (st.hasMoreTokens()) {
                    targetDir = st.nextToken();
                }

                req.setMethod(RequestMethodEnum.USER_LIST_SERVER_FILES);
                req.setData(targetDir);
                out.writeObject(req);

                resp = (Response) input.readObject();

                if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                    System.out.println(resp.getData());
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }
            } else if (cmd.equalsIgnoreCase("cd")) {
                String targetDir = st.nextToken();
                currLocalDir = FileUtil.getNextCWD(targetDir, currLocalDir);
            } else if (cmd.equalsIgnoreCase("scd")) {
                if (!hasSession()) {
                    continue;
                }

                String targetDir = st.nextToken();
                req.setMethod(RequestMethodEnum.USER_CHANGE_CWD);
                req.setData(targetDir);
                out.writeObject(req);
                resp = (Response) input.readObject();

                if (resp.getStatus() == ResponseStatusEnum.SUCCESS) {
                    session.setCurrentDir(resp.getData());
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }
            } else {
                System.out.println("Unknown command '" + line + "'");
            }

            if (serverConfigured) {
                out.flush();
                out.reset();
            }

            System.out.print(cmdPrefix(user, currLocalDir));
        }
    }

    public Client() throws IOException, ClassNotFoundException {
        run();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
    }
}
