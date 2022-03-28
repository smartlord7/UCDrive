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
import java.util.StringTokenizer;

public class Client {
    private void showErrors(HashMap<String, String> errors) {
        for (String key : errors.keySet()) {
            System.out.println(key + ": " + errors.get(key));
        }
    }

    public void run() throws IOException, ClassNotFoundException {
        String line;
        String ip;
        boolean serverConfigured = false;
        String currDir = System.getProperty("user.dir");
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


        while ((line = in.readLine()) != null && line.length() > 0) {
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
                    System.out.println("User already logged in!");
                    continue;
                }

                if (serverConfigured) {
                    System.out.println("Server not configured!");
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

            } else if (serverConfigured && line.equalsIgnoreCase("cpwd")) {
                if (!user.isAuth()) {
                    System.out.println("User not logged in!");
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
                    System.out.println("Password changed succesfully!");
                } else {
                    errors = resp.getErrors();
                    showErrors(errors);
                }
            } else if (line.equalsIgnoreCase("ls")) {
                System.out.println(FileUtil.listCurrDirFiles(new File(currDir)));
            } else if (line.toLowerCase().startsWith("cd")) {
                st = new StringTokenizer(line);
                st.nextToken();
                String targetDir = st.nextToken();

                if (targetDir.equals("..")) {
                    currDir = FileUtil.backDir(currDir);
                }
            }

            if (serverConfigured) {
                out.flush();
                out.reset();
            }
        }
    }

    public Client() throws IOException, ClassNotFoundException {
        run();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
    }
}
