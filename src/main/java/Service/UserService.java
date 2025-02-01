package Service;

import org.h2.engine.User;

import DAO.MessageDAO;
import DAO.UserDAO;
import Model.Account;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }
    /**
     * Register User
     * returning -1 means failure
     */
    public Account registerService(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.length() < 4) {
            return null;
        } 
        if (UserDAO.userExists(username)) {
            return null;
        }
        int accountID = UserDAO.registerUser(username, password);
        if (accountID == -1) {
            return null;
        }
        return new Account(accountID, username, password);
    }

    public static int loginService(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null) {
            return -1;
        }
        return UserDAO.loginDao(username, password);
    }
}