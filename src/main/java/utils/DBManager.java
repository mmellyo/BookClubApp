package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/nbookinidb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private DBManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}
