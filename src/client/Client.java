package client;

import com.google.gson.Gson;
import datalayer.model.User.User;
import protocol.Request;
import protocol.RequestMethodEnum;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Request req = new Request();
        Gson gson = new Gson();
        User user = new User();
        user.setUserName("administrator");
        user.setPassword("administrator123##");

        try (Socket s = new Socket("0.0.0.0", 8000)) {
            ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));

            req.setMethod(RequestMethodEnum.USER_AUTHENTICATION);
            req.setData(gson.toJson(user));
            out.writeObject(req);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
