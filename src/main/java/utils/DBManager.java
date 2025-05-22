package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/nbookinidb";
    private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "Mellybookclub";

    private DBManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}
