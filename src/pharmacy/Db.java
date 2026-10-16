package pharmacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static final String SERVER_URL =
        "jdbc:mysql://localhost:3306/";

    private static final String DATABASE =
        "pharmacy_management";

    private static final String USER =
        System.getenv().getOrDefault(
            "PHARMACY_DB_USER", "pharmacy_app"
        );

    private static final String PASSWORD =
        System.getenv().getOrDefault(
            "PHARMACY_DB_PASSWORD", ""
        );

    private Db() {}

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
            SERVER_URL + DATABASE, USER, PASSWORD
        );
    }
}