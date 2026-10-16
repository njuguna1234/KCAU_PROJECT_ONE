package pharmacy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {}

    public static void initialize() throws SQLException {

        try (Connection conn = Db.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(60) NOT NULL UNIQUE,
                    password_hash CHAR(64) NOT NULL,
                    role VARCHAR(30) NOT NULL DEFAULT 'PHARMACIST',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medicines (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    category VARCHAR(80) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    quantity INT NOT NULL DEFAULT 0,
                    expiry_date DATE NOT NULL,
                    low_stock_level INT NOT NULL DEFAULT 10,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,
                    CONSTRAINT chk_medicine_price CHECK (price >= 0),
                    CONSTRAINT chk_medicine_quantity CHECK (quantity >= 0),
                    CONSTRAINT chk_low_stock CHECK (low_stock_level >= 0)
                )
                """);
        }

        createDemoAdminIfMissing();
    }

    private static void createDemoAdminIfMissing()
            throws SQLException {

        String checkSql =
            "SELECT id FROM users WHERE username = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement check =
                 conn.prepareStatement(checkSql)) {

            check.setString(1, "admin");

            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }

            String insertSql =
                "INSERT INTO users " +
                "(username, password_hash, role) " +
                "VALUES (?, ?, ?)";

            try (PreparedStatement insert =
                     conn.prepareStatement(insertSql)) {

                insert.setString(1, "admin");
                insert.setString(
                    2,
                    PasswordUtil.sha256("admin123")
                );
                insert.setString(3, "ADMIN");

                insert.executeUpdate();
            }
        }
    }
}