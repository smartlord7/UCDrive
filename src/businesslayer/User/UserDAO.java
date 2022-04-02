/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package businesslayer.User;

import businesslayer.base.BaseDAO;
import businesslayer.base.DAOResult;
import businesslayer.base.DAOResultStatusEnum;
import datalayer.model.User.User;
import util.Const;
import util.Hasher;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Class that has the user DAO methods.
 */

public class UserDAO implements BaseDAO, Serializable{

    // region Public properties

    public static Connection connection;

    // endregion Public properties

    // region Public methods

    /**
     * Method that creates the user in the database.
     * @param user is the user.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static DAOResult create(User user) throws NoSuchAlgorithmException, NoSuchMethodException, SQLException {
        String sql;
        PreparedStatement stmt;

        sql = "INSERT INTO [User] (UserName, PasswordHash, CreateDate) VALUES (?, ?, CURRENT_TIMESTAMP)";

        stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getUserName());
        stmt.setString(2, Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM));

        stmt.executeUpdate();
        connection.commit();

        return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null, user,
                UserDAO.class, User.class, UserDAO.class.getMethod("create", User.class).getName());
    }

    /**
     * Method that changes the usre password in the database.
     * @param user is the user.
     * @return -1 if wrong username, -2 if wrong password, 0 if success.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static DAOResult changePassword(User user) throws NoSuchAlgorithmException, NoSuchMethodException, SQLException {
        String sql;
        String method;
        PreparedStatement stmt;

        method = UserDAO.class.getMethod("changePassword", User.class).getName();
        sql = "SELECT UserId, PasswordHash FROM [USER] WHERE UserName = ?";

        stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getUserName());

        ResultSet res = stmt.executeQuery();

        int userId = -1;
        String passwordHash = null;

        while (res.next()) {
            userId = res.getInt(1);
            passwordHash = res.getString(2);
        }

        if (userId == -1) {
            // Wrong userName
            return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null,
                    user, UserDAO.class,  User.class, method, -1);
        }

        if (!Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM).equals(passwordHash)) {
            // Wrong password
            return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null, user,
                    UserDAO.class, User.class, method, -2);
        }

        stmt = connection.prepareStatement("UPDATE [User] SET PasswordHash = ? WHERE UserId = ?");
        stmt.setString(1, Hasher.hashString(user.getNewPassword(), Const.PASSWORD_HASH_ALGORITHM));
        stmt.setInt(2, userId);
        stmt.executeUpdate();
        connection.commit();


        return new DAOResult(false, DAOResultStatusEnum.SUCCESS, null, user,
                UserDAO.class, User.class, method);
    }

    /**
     * Method that authenticates the user in the database.
     * @param user is the user.
     * @return -1 if wrong username, -2 if wrong password, 0 if success.
     * @throws SQLException - whenever a database related error occurs.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static DAOResult authenticate(User user) throws SQLException, NoSuchAlgorithmException, NoSuchMethodException {
        String passwordHash;
        String sql;
        PreparedStatement stmt;
        ResultSet res;
        String currMethod = UserDAO.class.getMethod("authenticate", User.class).getName();

        sql = "SELECT UserId, PasswordHash FROM [User] WHERE UserName = ? ";
        stmt = connection.prepareStatement(sql);

        stmt.setString(1, user.getUserName());
        res = stmt.executeQuery();

        passwordHash = null;

        while (res.next()) {
            user.setUserId(res.getInt(1));
            passwordHash = res.getString(2);
        }

        if (passwordHash == null) {
            // Wrong userName
            return new DAOResult(true, DAOResultStatusEnum.SUCCESS, null, user,
                    UserDAO.class, User.class,  currMethod, -2);
        }

        if (!Hasher.hashString(user.getPassword(), Const.PASSWORD_HASH_ALGORITHM).equals(passwordHash)) {
            // Wrong password
            return new DAOResult(true, DAOResultStatusEnum.SUCCESS, null, user,
                    UserDAO.class, User.class, currMethod, -2);
        }

        return new DAOResult(true, DAOResultStatusEnum.SUCCESS, null, user,
                UserDAO.class, User.class, currMethod);
    }

    // endregion Public methods

}
