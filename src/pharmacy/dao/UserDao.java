package pharmacy.dao;

import pharmacy.Db;
import pharmacy.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public boolean authenticate(
            String username,
            String password) throws SQLException {

        String sql =
            "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                    && PasswordUtil.sha256(password)
                        .equalsIgnoreCase(
                            rs.getString("password_hash")
                        );
            }
        }
    }
}