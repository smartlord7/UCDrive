package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;
import protocol.ResponseStatusEnum;
import util.FileUtil;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

public class Client {
    private void showErrors(HashMap<String, String> errors) {
        for (String key : errors.keySet()) {
            System.out.println(key + ": " + errors.get(key));
        }
    }

    private String cmdPrefix(User user, String currLocalDir) {
        return user.isAuth() ? user.getUserName() + "@" : "" + "UCDrive~\\" + currLocalDir + "\n$ ";
    }

    public void run() throws IOException, ClassNotFoundException {
        String line;
        String ip;
        boolean serverConfigured = false;
        String currLocalDir = System.getProperty("user.dir");
        String currRemoteDir = null;
        int port;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        ObjectOutputStream out = null;
        ObjectInputStream input = null;
        StringTokenizer st;
        Request req = new Request();
        Response resp;
        HashMap<String, String> errors;
        User user = new User();
        Gson gson = new Gson();

        System.out.print(cmdPrefix(user, currLocalDir));
        while (!(line = in.readLine()).equalsIgnoreCase("exit")) {
            if (line.equalsIgnoreCase("config server")) {
                System.out.println("Server IP: ");
                ip = in.readLine();
                System.out.println("Server port: ");
                port = Integer.parseInt(in.readLine());
                serverConfigured = true;

                try (Socket s = new Socket(ip, port)) {
                    out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));
                    input = new ObjectInputStream(new DataInputStream(s.getInputStream()));
                }
            } else if (line.equalsIgnoreCase("auth")) {
                if (user.isAuth()) {
                    System.out.println("Error: user already logged in!");
                    continue;
                }

                if (!serverConfigured) {
                    System.out.println("Error: server not configured!");
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
                    System.out.println("User " + user.getUserName() + " authenticated successfully!");
                    user.setAuth(true);
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }

            } else if (line.equalsIgnoreCase("cpwd")) {
                if (!serverConfigured) {
                    System.out.println("Error: server not configured");
                    continue;
                }

                if (!user.isAuth()) {
                    System.out.println("Error: user not logged in!");
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
            } else if (line.toLowerCase().startsWith("ls")) {
                st = new StringTokenizer(line);
                String dir = currLocalDir;

                st.nextToken();
                if (st.hasMoreTokens()) {
                    dir = st.nextToken();
                }

                File file = new File(dir);

                if (!file.isDirectory() || !file.exists()) {
                    System.out.println("Error: no directory '" + dir + "' found!");
                } else {
                    System.out.println(FileUtil.listDirFiles(file));
                }
            } else if (line.toLowerCase().startsWith("cd")) {
                st = new StringTokenizer(line);
                st.nextToken();
                String targetDir = st.nextToken();
                boolean validDir = false;

                File file = new File(targetDir);
                if (!file.isDirectory() || !file.exists()) {
                    file = new File(currLocalDir + "\\" + targetDir);

                    if (!file.isDirectory() || !file.exists()) {
                        System.out.println("Error: no directory '" + targetDir + "' found!");
                    } else {
                        validDir = true;
                    }

                } else {
                    validDir = true;
                }

                if (validDir) {
                    currLocalDir = file.getCanonicalPath();
                    System.setProperty("user.dir", currLocalDir);
                }
            } else {
                System.out.println("Unkown command '" + line + "'");
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
