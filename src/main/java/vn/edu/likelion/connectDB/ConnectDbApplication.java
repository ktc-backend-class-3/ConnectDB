package vn.edu.likelion.connectDB;

import java.sql.*;

public class ConnectDbApplication {

	public static void main(String[] args) {
		Connect conn = null;
		PreparedStatement stat = null;
		ResultSet resultSet = null;

		insertStudentDB(conn, stat, resultSet);
		selectStudentDB(conn, stat, resultSet);

	}

	private static void insertStudentDB(Connect conn, PreparedStatement stat, ResultSet resultSet) {
		try {
			String query = "insert into student values (?, ?, ?)";
			conn = new Connect();
			// Gửi câu lệnh truy vấn tới database
			stat = conn.openConnect().prepareStatement(query);
			stat.setInt(1, 2);
			stat.setString(2, "Tấn");
			stat.setInt(3, 20);
			// Thực hiện truy vấn
			int result = stat.executeUpdate();

			// Xử lý kết quả truy vấn
			if (result > 0) {
				System.out.println("Insert OK");
				System.out.println("-------------------------");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				conn.closeConnect();
				if (stat != null) stat.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}

	private static void selectStudentDB(Connect conn, PreparedStatement stat, ResultSet resultSet) {
		try {
			conn = new Connect();
			// Gửi câu lệnh truy vấn tới database
			stat = conn.openConnect().prepareStatement("select * from student");
			// Thực hiện truy vấn
			resultSet = stat.executeQuery();

			// Xử lý kết quả truy vấn
			while (resultSet.next()) {
				System.out.println("Topic: " + resultSet.getString(1));
				System.out.println("Seq: " + resultSet.getString(2));
				System.out.println("Info: " + resultSet.getString(3));
				System.out.println("----------------------------------");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				conn.closeConnect();
				if (stat != null) stat.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}

}
