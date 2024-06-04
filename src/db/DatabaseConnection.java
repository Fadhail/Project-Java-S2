package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // connect to the database
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // method to connect to the database
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // make a connection to the database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // print the error message
            e.printStackTrace();
        }
        return connection;
    }
}
