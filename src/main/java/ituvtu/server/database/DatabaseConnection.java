package ituvtu.server.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    private static DatabaseConnection instance;

    private DatabaseConnection() { }

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
            InputStream is = new FileInputStream("src/main/resources/ituvtu/server/config.properties");
            Properties props = new Properties();
            props.load(is);

            String url = props.getProperty("db.url") + "?autoReconnect=true";
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load database properties: " + e);
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

