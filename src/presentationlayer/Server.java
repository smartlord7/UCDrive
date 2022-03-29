package presentationlayer;

import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import com.google.gson.Gson;
import datalayer.enumerate.DirectoryPermissionEnum;
import datalayer.model.User.User;
import datalayer.model.User.UserSession;
import protocol.Request;
import protocol.Response;
import protocol.ResponseStatusEnum;
import server.ClientCommandConnection;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import static util.FileUtil.getFreeSpace;

public class Server {
    private static Gson gson = new Gson();

    private static void checkUserCredentials(Response resp, User user, int result) {
        if (result == 0) {
            resp.setStatus(ResponseStatusEnum.SUCCESS);
        } else {
            resp.setStatus(ResponseStatusEnum.ERROR);
            HashMap<String, String> errors = new HashMap<>();
            if (result == -1) {
                errors.put("UserNotFound", "User with username " + user.getUserName() + " not found");
            } else if (result == -2) {
                errors.put("WrongPassword", "Wrong password");
            }
            resp.setErrors(errors);
        }
    }

    public static Response authUser(Request req) throws SQLException, NoSuchAlgorithmException {
        int loginResult;
        String lastSessionDir;
        User user;
        UserSession userSession;
        Response resp;

        resp = new Response();
        user = gson.fromJson(req.getData(), User.class);
        loginResult = UserDAO.authenticate(user);

        checkUserCredentials(resp, user, loginResult);
        lastSessionDir = SessionLogDAO.getDirectoryFromLastSession(user.getUserId());

        if (lastSessionDir == null) {
            lastSessionDir = System.getProperty("user.dir");
        }

        userSession = new UserSession(user.getUserId(), lastSessionDir);
        resp.setData(gson.toJson(userSession));

        return resp;
    }

    public static Response changePassword(Request req) throws NoSuchAlgorithmException {
        Response resp = new Response();
        User user = gson.fromJson(req.getData(), User.class);
        int result = UserDAO.changePassword(user);

        checkUserCredentials(resp, user, result);

        return resp;
    }

    public static Response listDirFiles(Request req) {
        Response resp = new Response();
        String dir = req.getData();
        File f;

        if (dir != null && dir.length() != 0) {
            f = new File(dir);

            if (!f.exists() || !f.isDirectory()) {
                resp.setStatus(ResponseStatusEnum.ERROR);
                HashMap<String, String> errors = new HashMap<>();
                errors.put("DirectoryNotFound", "Directory '" + dir + "' not found");
                resp.setErrors(errors);
            } else {
                resp.setStatus(ResponseStatusEnum.SUCCESS);
                resp.setData(FileUtil.listDirFiles(f));
            }
        } else {
            f = new File(System.getProperty("user.dir"));
            resp.setStatus(ResponseStatusEnum.SUCCESS);
            resp.setData(FileUtil.listDirFiles(f));
        }

        return resp;
    }

    public static Response changeWorkingDir(Request req, UserSession session) throws IOException {
        Response resp = new Response();
        String dir = req.getData();
        File f;

        if (dir != null && dir.length() != 0) {
            f = new File(dir);
            if (!f.exists() || !f.isDirectory()) {
                resp.setStatus(ResponseStatusEnum.ERROR);
                HashMap<String, String> errors = new HashMap<>();
                errors.put("DirectoryNotFound", "Directory '" + dir + "' not found");
                resp.setErrors(errors);
            } else {
                String nextCWD = FileUtil.getNextCWD(dir, session.getCurrentDir());
                session.setCurrentDir(nextCWD);
                resp.setData(nextCWD);
                resp.setStatus(ResponseStatusEnum.SUCCESS);
            }
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("NoSpecifiedDirectory", "No directory was specified");
            resp.setErrors(errors);
        }

        return resp;
    }

    public static DirectoryPermissionEnum getDirectoryPermission(String currDir, int userId) throws SQLException {
        return UserDAO.getDirectoryPermission(userId, currDir);
    }

    public static void downloadFiles(File curDir) throws SQLException {
        int id = 4;
        DirectoryPermissionEnum perm = getDirectoryPermission(curDir.getPath(), id);
        System.out.println("Select the file to download: ");
        if(perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.READ_WRITE && getFreeSpace(curDir) > curDir.length()){
            System.out.println("Permission to download");
            //download();
        }else if(perm == DirectoryPermissionEnum.WRITE || perm == DirectoryPermissionEnum.NONE){
            System.out.println("No permission to download");
        }else if(getFreeSpace(curDir) < curDir.length()){
            System.out.println("No space left");
        }
    }

    public static void uploadFiles(File curDir) throws SQLException    {
        int id = 4;
        DirectoryPermissionEnum perm = getDirectoryPermission(curDir.getPath(), id);
        long space = getFreeSpace(curDir);
        System.out.println("Select the file to upload: ");
        if(perm == DirectoryPermissionEnum.WRITE || perm == DirectoryPermissionEnum.READ_WRITE && getFreeSpace(curDir) > curDir.length() ){
            System.out.println("Permission to upload");
            //upload();
        }else if(perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.NONE){
            System.out.println("No permission to upload");
        }else if(getFreeSpace(curDir) < curDir.length()){
            System.out.println("No space left");
        }
    }

    public static String menu(){
        return """
                \t\t\t\t       Client
                \t\t\t\t\t   MENU
                \t\t\t ________________________________
                \t\t\t|                                |
                \t\t\t|1 -> REGISTER  |
                \t\t\t|                                |
                \t\t\t|2 -> LOGIN    |
                \t\t\t|                                |
                \t\t\t|3 -> UPLOAD                 |
                \t\t\t|                                |
                \t\t\t|4 -> DOWNLOAD        |
                \t\t\t|________________________________|
                \s
                \t\t\t5 - EXIT
                                    \s""";
    }
}
