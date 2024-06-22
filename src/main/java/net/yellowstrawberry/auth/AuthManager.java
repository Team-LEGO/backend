package net.yellowstrawberry.auth;

import net.yellowstrawberry.db.SQLCommunicator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthManager {
    public static boolean register(String username, String id, String password) {
        int i = SQLCommunicator.executeUpdate("INSERT INTO `users` (`id`, `pass`, `name`, `age`, `job`, `interests`, `categories`, `joined`) " +
                "VALUES (?, ?, ?, 0, '알 수 없음', '[]', '[]', CURRENT_TIMESTAMP());", username, id, password);
        return i ==  1;
    }

    public static boolean login(String id, String password) {
        try(ResultSet set = SQLCommunicator.executeQuery("SELECT `pass` FROM `users` WHERE `id`=?;", id)) {
            if(set.next()) {
                return set.getString("pass").equals(password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
