package me.yellowbear.uwujobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:plugins/uwuJobs/uwu.db");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
