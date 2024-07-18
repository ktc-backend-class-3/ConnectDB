package vn.edu.likelion.connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private String user = "system";
    private String pass = "123456";
    private Connection conn = null;

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
