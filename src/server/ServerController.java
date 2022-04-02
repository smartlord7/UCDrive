package server;

import businesslayer.FilePermission.FilePermissionDAO;
import businesslayer.SessionLog.SessionLogDAO;
import businesslayer.User.UserDAO;
import com.google.gson.Gson;
import datalayer.enumerate.FilePermissionEnum;
import datalayer.model.SessionLog.SessionLog;
import datalayer.model.User.ClientUserSession;
import datalayer.model.User.User;
import datalayer.enumerate.FileOperationEnum;
import protocol.clientserver.Request;
import protocol.clientserver.Response;
import protocol.clientserver.ResponseStatusEnum;
import server.struct.ServerUserSession;
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

/**
 * Class that has the verification methods of the server.
 */
public class ServerController {

    // region Private properties

    private static final Gson gson = new Gson();

    // endregion Private properties

    // region Private methods

    /**
     * Method that verifies the user login credentials.
     * @param resp is the response status.
     * @param user user login name.
     * @param result is the checking result.
     */
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

    /**
     * Method that checks if a directory exists or not.
     * @param resp is the response status.
     * @param dir the chosen directory to be verified.
     * @param f is the file name.
     * @return true if everything executed sucefully and false if there was an error.
     */
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

    /**
     * Method used to set up the Server-client session.
     * @param session is the current user connected session.
     * @param userId is the user id.
     * @param initialDir is the initial user dir.
     * @param user is the user login name.
     * @param resp is the response status
     * @return the final response status.
     */
    private static Response initSession(ServerUserSession session, int userId, String initialDir, User user, Response resp) {
        ClientUserSession userSession;
        userSession = new ClientUserSession(userId, initialDir);
        session.setUserId(user.getUserId());
        session.setCurrentDir(initialDir);
        userSession.setUserId(user.getUserId());
        userSession.setCurrentDir(initialDir);
        resp.setStatus(ResponseStatusEnum.SUCCESS);
        resp.setContent(gson.toJson(userSession));

        return resp;
    }

    // endregion Private methods

    // region Public methods

    /**
     * Method used to register a user.
     * @param req is the request sent to the server.
     * @param session is the current server session.
     * @return the logged in now registered user.
     * @throws SQLException - whenever a database related error occurs.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     */
    public static Response createUser(Request req, ServerUserSession session) throws SQLException, NoSuchAlgorithmException, IOException {
        int userId;
        String initialDir;
        User user;
        Response resp;

        resp = new Response();
        user = gson.fromJson(req.getContent(), User.class);
        UserDAO.create(user);
        UserDAO.authenticate(user);
        userId = user.getUserId();

        initialDir = Const.USERS_FOLDER_NAME + "\\" + user.getUserName();
        Files.createDirectory(Paths.get(initialDir));
        FilePermissionDAO.create(userId, initialDir, FilePermissionEnum.READ_WRITE);

        return initSession(session, userId, initialDir, user, resp);
    }

    /**
     * Method used to authenticate the user.
     * @param req is the request sent to the server.
     * @param session is the current server session.
     * @return the now authenticated user.
     * @throws SQLException - whenever a database related error occurs.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static Response authUser(Request req, ServerUserSession session) throws SQLException, NoSuchAlgorithmException, IOException {
        int loginResult;
        int userId;
        String currentDir;
        Path p;
        User user;
        Response resp;

        resp = new Response();
        user = gson.fromJson(req.getContent(), User.class);
        loginResult = UserDAO.authenticate(user);

        checkUserCredentials(resp, user, loginResult);

        if (resp.getStatus() != ResponseStatusEnum.SUCCESS) {
            return resp;
        }

        userId = user.getUserId();
        currentDir = Const.USERS_FOLDER_NAME + "\\" + user.getUserName();
        p = Paths.get(currentDir);

        if (!Files.exists(Paths.get(currentDir))) {
            Files.createDirectory(p);
        }

        return initSession(session, userId, currentDir, user, resp);
    }

    /**
     * Method used to logout the user.
     * @param req is the server request.
     * @param session is the current server session.
     * @return the response status.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static Response logoutUser(Request req, ServerUserSession session) throws SQLException {
        Response resp;
        SessionLog sessionLog;

        resp = new Response();
        sessionLog = gson.fromJson(req.getContent(), SessionLog.class);
        sessionLog.setLastDirectory(session.getCurrentDir());
        sessionLog.setUserId(session.getUserId());

        SessionLogDAO.create(sessionLog);
        session.setUserId(0);
        session.setFileMetadata(null);
        resp.setStatus(ResponseStatusEnum.SUCCESS);

        return resp;
    }

    /**
     * Method used to change the user password.
     * @param req is the server request.
     * @return the response status.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static Response changeUserPassword(Request req) throws NoSuchAlgorithmException {
        Response resp = new Response();
        User user = gson.fromJson(req.getContent(), User.class);
        int result = UserDAO.changePassword(user);

        checkUserCredentials(resp, user, result);

        return resp;
    }

    /**
     * Method used to list the directory files.
     * @param req is the server request.
     * @param session is the current server session.
     * @return the response status.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static Response listDirFiles(Request req, ServerUserSession session) throws SQLException {
        boolean validDir = true;
        FilePermissionEnum perm;
        String dir = req.getContent();
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
            perm = FilePermissionDAO.getPermission(session.getUserId(), dir);

            if (perm == FilePermissionEnum.READ || perm == FilePermissionEnum.READ_WRITE) {
                resp.setStatus(ResponseStatusEnum.SUCCESS);
                resp.setContent(FileUtil.listDirFiles(f));
            } else {
                resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
                errors = new HashMap<>();
                errors.put("NoReadPermission", "User has no permission to read from directory '" + dir + "'");
            }
        }

        resp.setErrors(errors);

        return resp;
    }

    /**
     * Method used to change the working directory.
     * @param req is the server request.
     * @param session is the server current session.
     * @return the response status.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static Response changeWorkingDir(Request req, ServerUserSession session) throws IOException, SQLException {
        boolean validDir;
        String targetDir;
        FilePermissionEnum perm;
        Response resp;
        HashMap<String, String> errors = null;
        File f;

        validDir = true;
        resp = new Response();
        targetDir = req.getContent();

        if (targetDir != null && targetDir.length() != 0) {
            f = new File(targetDir);

            if (!f.exists() || !f.isDirectory()) {
                targetDir = System.getProperty("user.dir") + "\\" + targetDir;
                f = new File(targetDir);

                validDir = directoryExists(resp, targetDir, f);
            }

            if (validDir) {
                String nextCWD = FileUtil.getNextCWD(targetDir, session.getCurrentDir());
                perm = FilePermissionDAO.getPermission(session.getUserId(), nextCWD);

                if (perm == FilePermissionEnum.READ || perm == FilePermissionEnum.READ_WRITE) {
                    session.setCurrentDir(nextCWD);
                    resp.setStatus(ResponseStatusEnum.SUCCESS);
                    resp.setContent(nextCWD);
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

    /**
     * Method used to upload files.
     * @param req is the server request.
     * @param session is the server session.
     * @return the response status.
     * @throws SQLException - whenever a database related error occurs.
     */
    public static Response uploadFiles(Request req, ServerUserSession session) throws SQLException {
        int userId;
        String dir;
        FilePermissionEnum perm;
        Response resp;
        HashMap<String, String> errors;
        FileMetadata fileMeta;

        userId = session.getUserId();
        dir = session.getCurrentDir();
        perm = FilePermissionDAO.getPermission(userId, dir);
        resp = new Response();
        errors = new HashMap<>();

        if (perm == FilePermissionEnum.WRITE || perm == FilePermissionEnum.READ_WRITE){
            fileMeta = gson.fromJson(req.getContent(), FileMetadata.class);
            session.setFileMetadata(fileMeta);
            resp.setStatus(ResponseStatusEnum.SUCCESS);
            session.getFileMetadata().setOp(FileOperationEnum.UPLOAD);
            session.getSyncObj().change();
        } else if (perm == FilePermissionEnum.READ || perm == FilePermissionEnum.NONE){
            resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
            errors.put("NoWritePermission", "User has no permission to write in directory '" +  dir + "'");
            resp.setErrors(errors);
        }

        return resp;
    }

    /**
     * Method used to download files.
     * @param req is the server request.
     * @param session is the server session.
     * @return the response status.
     * @throws SQLException - whenever a database related error occurs.
     * @throws IOException - whenever an input or output operation is failed or interpreted.
     */
    public static Response downloadFiles(Request req, ServerUserSession session) throws SQLException, IOException {
        int userId;
        String currDir;
        String fileName;
        String filePath;
        Path p;
        FilePermissionEnum perm;
        Response resp;
        HashMap<String, String> errors;
        FileMetadata fileMeta;

        userId = session.getUserId();
        currDir = session.getCurrentDir();
        resp = new Response();
        errors = new HashMap<>();

        fileMeta = gson.fromJson(req.getContent(), FileMetadata.class);
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


        perm = FilePermissionDAO.getPermission(userId, currDir + "\\" + fileMeta.getFileName());

        if (perm == FilePermissionEnum.READ || perm == FilePermissionEnum.READ_WRITE){
            session.setFileMetadata(fileMeta);
            resp.setStatus(ResponseStatusEnum.SUCCESS);
            resp.setContent(gson.toJson(fileMeta));
            session.getFileMetadata().setOp(FileOperationEnum.DOWNLOAD);
            session.getSyncObj().change();
        } else if (perm == FilePermissionEnum.WRITE || perm == FilePermissionEnum.NONE){
            resp.setStatus(ResponseStatusEnum.UNAUTHORIZED);
            errors.put("NoReadPermission", "User has no permission to read file '" +  fileName + "'");
            resp.setErrors(errors);
        }

        return resp;
    }

    /**
     * Method that prints the user menu.
     * @return the user menu.
     */
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

    // endregion Public methods

}
