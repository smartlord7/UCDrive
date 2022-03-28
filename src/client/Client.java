package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import protocol.Request;
import protocol.RequestMethodEnum;
import protocol.Response;
import protocol.ResponseStatusEnum;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    private void showErrors(HashMap<String, String> errors) {
        for (String key : errors.keySet()) {
            System.out.println(key + ": " + errors.get(key));
        }
    }

    public void run() {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Request req = new Request();
        Response resp;
        HashMap<String, String> errors;
        User user = new User();
        Gson gson = new Gson();

        try (Socket s = new Socket("0.0.0.0", 8000)) {
            ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));
            ObjectInputStream input = new ObjectInputStream(new DataInputStream(s.getInputStream()));


            while ((line = in.readLine()) != null && line.length() > 0) {
                if (line.equalsIgnoreCase("auth")) {
                    if (user.isAuth()) {
                        System.out.println("User already logged in!");
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
                }

                out.flush();
                out.reset();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Client() {
        run();
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
