package ituvtu.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;
    private static DatabaseConnection instance;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    private DatabaseConnection() { }

    public static void initialize(String url, String user, String password) {
        dbUrl = url + "?autoReconnect=true";
        dbUser = user;
        dbPassword = password;
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        if (connection == null || !isValid(connection)) {
            connection = createNewConnection();
        }
        return connection;
    }

    private static Connection createNewConnection() {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    private static boolean isValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}
