package server;

import businesslayer.DirectoryPermission.DirectoryPermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import com.google.gson.Gson;
import datalayer.enumerate.DirectoryPermissionEnum;
import datalayer.model.SessionLog.SessionLog;
import datalayer.model.User.ClientUserSession;
import datalayer.model.User.User;
import datalayer.enumerate.FileOperationEnum;
import protocol.Request;
import protocol.Response;
import protocol.ResponseStatusEnum;
import util.Const;
import util.FileMetadata;
import util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerController {
    private static Gson gson = new Gson();

    private static void checkUserCredentials(Response resp, User user, int result) {
        if (result == 0) {
            resp.setStatus(ResponseStatusEnum.SUCCESS);
        } else {
            resp.setStatus(ResponseStatusEnum.ERROR);
            HashMap<String, String> errors = new HashMap<>();
            if (result == -1) {
                errors.put("UserNotFound", "User '" + user.getUserName() + "' not found");
            } else if (result == -2) {
                errors.put("WrongPassword", "Wrong password");
            }
            resp.setErrors(errors);
        }
    }

    private static boolean directoryExists(Response resp, String dir, File f) {
        if (!f.exists() || !f.isDirectory()) {
            resp.setStatus(ResponseStatusEnum.ERROR);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("DirectoryNotFound", "Directory '" + dir + "' not found");
            resp.setErrors(errors);

            return false;
        }

        return true;
    }

    private static Response initSession(ServerUserSession session, int userId, String initialDir, User user, Response resp) {
        ClientUserSession userSession;
        userSession = new ClientUserSession(userId, initialDir);
        session.setUserId(user.getUserId());
        session.setCurrentDir(initialDir);
        userSession.setUserId(user.getUserId());
        userSession.setCurrentDir(initialDir);
        resp.setStatus(ResponseStatusEnum.SUCCESS);
        resp.setData(gson.toJson(userSession));

        return resp;
    }

    public static Response createUser(Request req, ServerUserSession session) throws SQLException, NoSuchAlgorithmException, IOException {
        int userId;
        String initialDir;
        User user;
        Response resp;

        resp = new Response();
        user = gson.fromJson(req.getData(), User.class);
        UserDAO.create(user);
        UserDAO.authenticate(user);
        userId = user.getUserId();

        initialDir = Const.USERS_FOLDER_NAME + "\\" + user.getUserName();
        Files.createDirectory(Paths.get(initialDir));
        DirectoryPermissionDAO.create(userId, initialDir, DirectoryPermissionEnum.READ_WRITE);

        return initSession(session, userId, initialDir, user, resp);
    }

    public static Response authUser(Request req, ServerUserSession session) throws SQLException, NoSuchAlgorithmException {
        int loginResult;
        int userId;
        String lastSessionDir;
        User user;
        Response resp;

        resp = new Response();
        user = gson.fromJson(req.getData(), User.class);
        loginResult = UserDAO.authenticate(user);

        checkUserCredentials(resp, user, loginResult);

        if (resp.getStatus() != ResponseStatusEnum.SUCCESS) {
            return resp;
        }

        userId = user.getUserId();
        lastSessionDir = Const.USERS_FOLDER_NAME + "\\" + user.getUserName();

        return initSession(session, userId, lastSessionDir, user, resp);
    }

    public static Response logoutUser(Request req, ServerUserSession session) throws SQLException {
        Response resp;
        SessionLog sessionLog;

        resp = new Response();
        sessionLog = gson.fromJson(req.getData(), SessionLog.class);
        sessionLog.setLastDirectory(session.getCurrentDir());
        sessionLog.setUserId(session.getUserId());

        SessionLogDAO.create(sessionLog);
        session.setUserId(0);
        session.setFileMetadata(null);
        resp.setStatus(ResponseStatusEnum.SUCCESS);

        return resp;
    }

    public static Response changeUserPassword(Request req) throws NoSuchAlgorithmException {
        Response resp = new Response();
        User user = gson.fromJson(req.getData(), User.class);
        int result = UserDAO.changePassword(user);

        checkUserCredentials(resp, user, result);

        return resp;
    }

    public static Response listDirFiles(Request req, ServerUserSession session) throws SQLException {
        boolean validDir = true;
        DirectoryPermissionEnum perm;
        String dir = req.getData();
        Response resp = new Response();
        HashMap<String, String> errors = null;
        File f;

        if (dir != null && dir.length() != 0) {
            f = new File(dir);

            if (!f.exists() || !f.isDirectory()) {
                dir = session.getCurrentDir() + "\\" + dir;
                f = new File(dir);

                validDir = directoryExists(resp, dir, f);
            }
        } else {
            dir = session.getCurrentDir();
            f = new File(dir);
        }

        if (validDir) {
            perm = DirectoryPermissionDAO.getPermission(session.getUserId(), dir);

            if (perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.READ_WRITE) {
                resp.setStatus(ResponseStatusEnum.SUCCESS);
                resp.setData(FileUtil.listDirFiles(f));
            } else {
                resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
                errors = new HashMap<>();
                errors.put("NoReadPermission", "User has no permission to read from directory '" + dir + "'");
            }
        }

        resp.setErrors(errors);

        return resp;
    }

    public static Response changeWorkingDir(Request req, ServerUserSession session) throws IOException, SQLException {
        boolean validDir;
        String targetDir;
        DirectoryPermissionEnum perm;
        Response resp;
        HashMap<String, String> errors = null;
        File f;

        validDir = true;
        resp = new Response();
        targetDir = req.getData();

        if (targetDir != null && targetDir.length() != 0) {
            f = new File(targetDir);

            if (!f.exists() || !f.isDirectory()) {
                targetDir = System.getProperty("user.dir") + "\\" + targetDir;
                f = new File(targetDir);

                validDir = directoryExists(resp, targetDir, f);
            }

            if (validDir) {
                String nextCWD = FileUtil.getNextCWD(targetDir, session.getCurrentDir());
                perm = DirectoryPermissionDAO.getPermission(session.getUserId(), nextCWD);

                if (perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.READ_WRITE) {
                    session.setCurrentDir(nextCWD);
                    resp.setStatus(ResponseStatusEnum.SUCCESS);
                    resp.setData(nextCWD);
                } else {
                    resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
                    errors = new HashMap<>();
                    errors.put("NoReadPermission", "User has no permission to read from directory '" + targetDir + "'");
                }
            }
        } else {
            resp.setStatus(ResponseStatusEnum.ERROR);
            errors = new HashMap<>();
            errors.put("NoSpecifiedDirectory", "No directory was specified");
        }

        resp.setErrors(errors);

        return resp;
    }

    public static Response uploadFiles(Request req, ServerUserSession session) throws SQLException {
        int userId;
        String dir;
        DirectoryPermissionEnum perm;
        Response resp;
        HashMap<String, String> errors;
        FileMetadata fileMeta;

        userId = session.getUserId();
        dir = session.getCurrentDir();
        perm = DirectoryPermissionDAO.getPermission(userId, dir);
        resp = new Response();
        errors = new HashMap<>();

        if (perm == DirectoryPermissionEnum.WRITE || perm == DirectoryPermissionEnum.READ_WRITE){
            fileMeta = gson.fromJson(req.getData(), FileMetadata.class);
            session.setFileMetadata(fileMeta);
            resp.setStatus(ResponseStatusEnum.SUCCESS);
            session.getFileMetadata().setOp(FileOperationEnum.UPLOAD);
            session.getSyncObj().change();
        } else if (perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.NONE){
            resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
            errors.put("NoWritePermission", "User has no permission to write in directory '" +  dir + "'");
            resp.setErrors(errors);
        }

        return resp;
    }

    public static Response downloadFiles(Request req, ServerUserSession session) throws SQLException, IOException {
        int userId;
        String currDir;
        String fileName;
        String filePath;
        Path p;
        DirectoryPermissionEnum perm;
        Response resp;
        HashMap<String, String> errors;
        FileMetadata fileMeta;

        userId = session.getUserId();
        currDir = session.getCurrentDir();
        resp = new Response();
        errors = new HashMap<>();

        fileMeta = gson.fromJson(req.getData(), FileMetadata.class);
        fileName = fileMeta.getFileName();
        filePath = System.getProperty("user.dir") + "\\" + currDir + "\\" + fileMeta.getFileName();
        p = Paths.get(filePath);

        if (!Files.exists(p)) {
            resp.setStatus(ResponseStatusEnum.ERROR);
            errors.put("FileNotFound", "File '" +  fileName + "' not found");
            resp.setErrors(errors);

            return resp;
        }
        fileMeta.setFileSize((int) Files.size(p));


        perm = DirectoryPermissionDAO.getPermission(userId, currDir);

        if (perm == DirectoryPermissionEnum.READ || perm == DirectoryPermissionEnum.READ_WRITE){
            session.setFileMetadata(fileMeta);
            resp.setStatus(ResponseStatusEnum.SUCCESS);
            resp.setData(gson.toJson(fileMeta));
            session.getFileMetadata().setOp(FileOperationEnum.DOWNLOAD);
            session.getSyncObj().change();
        } else if (perm == DirectoryPermissionEnum.WRITE || perm == DirectoryPermissionEnum.NONE){
            resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
            errors.put("NoReadPermission", "User has no permission to read from directory '" +  fileName + "'");
            resp.setErrors(errors);
        }

        return resp;
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