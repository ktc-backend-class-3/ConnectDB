package vn.edu.likelion.connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private String url;
    private String user;
    private String pass;
    private Connection conn = null;

    public Connect() {
        url = "jdbc:oracle:thin:@localhost:1521:xe";
        user = "system";
        pass = "123456";
    }

    public Connection openConnect() throws SQLException {
        conn = DriverManager.getConnection(url, user, pass);
        return conn;
    }

    public void closeConnect() throws SQLException {
        if (conn != null) conn.close();
    }

    public Connection getConnect() {
        return conn;
    }
}
