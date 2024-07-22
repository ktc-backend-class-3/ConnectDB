package vn.edu.likelion.connectDB;

import vn.edu.likelion.connectDB.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class Application {

    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<Student> studentList = null;
    private static User user = null;

    private static void init(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
        getStudents(conn, preparedStatement, resultSet);
        if (studentList == null) {
            BufferedReader bufferedReader = null;
            try {
                studentList = new ArrayList<>();
                conn.openConnect();
                String sqlQuery = "insert into tbl_students (fullname) values (?) ";

                FileReader fileReader = new FileReader("StudentsList.txt");
                bufferedReader = new BufferedReader(fileReader);
                String line;
                String[] arr = null;
                Student stu;

                while ((line = bufferedReader.readLine()) != null) {
                    stu = new Student();
                    arr = line.split("\t");
                    stu.setName(arr[1]);
                    studentList.add(stu);
                }

                preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
                for (Student student : studentList) {
                    preparedStatement.setString(1, student.getName());
                    preparedStatement.addBatch();
                }

                int[] results = preparedStatement.executeBatch();
                System.out.println("Đã insert: " + results.length + " records");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    if (conn != null) conn.closeConnect();
                    if (preparedStatement != null) preparedStatement.close();
                    if (resultSet != null) resultSet.close();
                    if (bufferedReader != null) bufferedReader.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connect conn = new Connect();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        // init du lieu, dua danh sach student vao database
        init(conn, preparedStatement, resultSet);

        while (true) {
            // menu chuong trinh
            showMenu();
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập số.");
                continue;
            }
            switch (choice) {
                case 1:
                    // dang ky
                    if (user == null) signup(conn, preparedStatement, resultSet);
                    else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 2:
                    // dang nhap
                    if (user == null) login(conn, preparedStatement, resultSet);
                    else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 3:
                    // diem danh
                    if (user != null) {
                        boolean insertPermission = user.getRole().getPermissions().stream()
                                .map(Permission::getPermission_name)
                                .anyMatch(permissionName -> "Insert".equals(permissionName));
                        if (insertPermission) attendace(conn, preparedStatement);
                        else System.out.println("Bạn không có quyền sử dụng chức năng này.");
                    } else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 4:
                    // thong ke hoc vien co mat
                    if (user != null) {
                        boolean insertPermission = user.getRole().getPermissions().stream()
                                .map(Permission::getPermission_name)
                                .anyMatch(permissionName -> "Select".equals(permissionName));
                        if (insertPermission) getAttendance(conn, preparedStatement, resultSet, 0);
                        else System.out.println("Bạn không có quyền sử dụng chức năng này.");
                    } else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 5:
                    // thong ke hoc vien vang mat
                    if (user != null) {
                        boolean insertPermission = user.getRole().getPermissions().stream()
                                .map(Permission::getPermission_name)
                                .anyMatch(permissionName -> "Select".equals(permissionName));
                        if (insertPermission) getAttendance(conn, preparedStatement, resultSet, 1);
                        else System.out.println("Bạn không có quyền sử dụng chức năng này.");
                    } else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 6:
                    // thong ke toan bo hoc vien
                    if (user != null) {
                        boolean insertPermission = user.getRole().getPermissions().stream()
                                .map(Permission::getPermission_name)
                                .anyMatch(permissionName -> "Select".equals(permissionName));
                        if (insertPermission) getAttendance(conn, preparedStatement, resultSet, 2);
                        else System.out.println("Bạn không có quyền sử dụng chức năng này.");
                    } else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
                    break;
                case 7:
                    // dang xuat
                    if (user != null) user = null;
                    break;
                case 0:
                    System.out.println("Thoát chương trình.");
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("========= MENU =========");
        if (user == null) {
            System.out.println("1. Đăng ký.");
            System.out.println("2. Đăng nhập.");
        }
        if (user != null) {
            System.out.println("3. Điểm danh.");
            System.out.println("4. Xem danh sách có mặt.");
            System.out.println("5. Xem danh sách vắng mặt.");
            System.out.println("6. Xem toàn bộ danh sách điểm danh.");
            System.out.println("7. Đăng xuất.");
        }
        System.out.println("0. Thoát chương trình.");
        System.out.println("========================");
        System.out.print("Chọn chức năng: ");
    }

    private static void signup(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            System.out.print("Nhập tên người dùng: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Tên người dùng không được để trống.");
                return;
            }
            System.out.print("Nhập mật khẩu: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Mật khẩu không được để trống.");
                return;
            }
            String passwordEncoding = Base64.getEncoder().encodeToString(password.getBytes());
            showRoles(conn, preparedStatement, resultSet);
            System.out.print("Nhập số thứ tự vai trò cho người dùng: ");
            String roleIdInput = scanner.nextLine().trim();
            int role_id;
            try {
                role_id = Integer.parseInt(roleIdInput);
            } catch (NumberFormatException e) {
                System.out.println("Role ID phải là một số nguyên.");
                return;
            }
            System.out.println("Đang trong quá trình đăng ký, vui lòng chờ trong giây lát.");

            String sqlQuery = "insert into tbl_users (username, password, role_id) values (?, ?, ?)";
            conn.openConnect();
            preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordEncoding);
            preparedStatement.setInt(3, role_id);
            int status = preparedStatement.executeUpdate();

            if (status > 0) System.out.println("Đăng ký thành công.");
            else System.out.println("Có lỗi trong quá trình đăng ký.");
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình đăng ký.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình đăng ký.");
            }
        }
    }

    private static void showRoles(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            System.out.println("Ứng dụng đang lấy danh sách vai trò, vui lòng chờ trong giây lát.");
            String sqlQuery = "select * from tbl_roles";
            conn.openConnect();
            preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.isBeforeFirst()) {
                int index = 1;
                while (resultSet.next()) {
                    System.out.println(index + " " + resultSet.getString("role_name"));
                    index++;
                }
            } else System.out.println("Không có dữ liệu trong hệ thống.");
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình lấy danh sách vai trò.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình lấy danh sách vai trò.");
            }
        }
    }

    private static void login(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            System.out.print("Nhập tên người dùng: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Tên người dùng không được để trống.");
                return;
            }

            System.out.print("Nhập mật khẩu: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Mật khẩu không được để trống.");
                return;
            }
            String passwordEncoding = Base64.getEncoder().encodeToString(password.getBytes());
            System.out.println("Đang trong quá trình đăng nhập, vui lòng chờ trong giây lát.");

            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("select u.username, r.role_name, p.permission_name ");
            sqlQuery.append("from tbl_users u ");
            sqlQuery.append("join tbl_roles r on u.role_id = r.id ");
            sqlQuery.append("join tbl_roles_permissions pr on r.id = pr.role_id ");
            sqlQuery.append("join tbl_permissions p on pr.permission_id = p.id ");
            sqlQuery.append("where u.username = ? and u.password = ? ");
            conn.openConnect();
            preparedStatement = conn.getConnect().prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, new String(passwordEncoding));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Role role = null;
                Permission permission = null;
                Set<Permission> permissionSet = new HashSet<>();
                while (resultSet.next()) {
                    if (user == null) {
                        user = new User();
                        user.setUsername(resultSet.getString("username"));
                    }
                    if (role == null) {
                        role = new Role();
                        role.setRole_name(resultSet.getString("role_name"));
                    }
                    permission = new Permission();
                    permission.setPermission_name(resultSet.getString("permission_name"));
                    permissionSet.add(permission);
                }
                if (role != null) {
                    role.setPermissions(permissionSet);
                    if (user != null) {
                        user.setRole(role);
                    }
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình đăng nhập.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình đăng nhập.");
            }
        }
    }

    private static void attendace(Connect conn, PreparedStatement preparedStatement) {
        ArrayList<Attendance> attendancesList = new ArrayList<>();
        Attendance attendance = null;
        LocalDate today = LocalDate.now();
        System.out.println("Vui lòng nhập 0 nếu học viên có mặt hoặc nhập 1 nếu học viên vắng mặt: ");
        for (Student student: studentList) {
            System.out.print("Học viên " + student.getName() + ": ");
            String attInput = scanner.nextLine().trim();
            int att;
            try {
                att = Integer.parseInt(attInput);
                if (att != 0 && att != 1) {
                    System.out.println("Vui lòng nhập 0 nếu học viên có mặt hoặc nhập 1 nếu học viên vắng mặt.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập chỉ nhập số 0 hoặc 1.");
                return;
            }
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setAttendance_date(Date.valueOf(today));
            attendance.setStatus(att);
            attendancesList.add(attendance);
        }

        try {
            System.out.println("Ứng dụng bắt đầu ghi nhận điểm danh, vui lòng chờ trong giây lát.");
            String sqlQuery = "insert into tbl_attendance (student_id, attendance_date, status) values (?, ?, ?)";
            conn.openConnect();
            preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
            for (Attendance atten: attendancesList) {
                preparedStatement.setInt(1, atten.getStudent().getId());
                preparedStatement.setDate(2, atten.getAttendance_date());
                preparedStatement.setInt(3, atten.getStatus());
                preparedStatement.addBatch();
            }
            int[] results = preparedStatement.executeBatch();
            System.out.println("Đã điểm danh xong " + results.length + " học viên.");
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình điểm danh.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình điểm danh.");
            }
        }
    }

    private static void getAttendance(Connect conn, PreparedStatement preparedStatement,
                                      ResultSet resultSet, int status) {
        ArrayList<Attendance> attendanceList = null;
        try {
            System.out.println("Ứng dụng đang lấy danh sách điểm danh, vui lòng chờ trong giây lát.");
            conn.openConnect();
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("select s.id, s.fullname, a.attendance_date, a.status from tbl_attendance a join tbl_students s on a.student_id = s.id");
            attendanceList = new ArrayList<>();
            Attendance attendance = null;
            Student student = null;

            if (status != 2) {
                sqlQuery.append(" where a.status = (?)");
                preparedStatement = conn.getConnect().prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, status);
            } else {
                preparedStatement = conn.getConnect().prepareStatement(sqlQuery.toString());
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                attendance = new Attendance();
                student = new Student();
                student.setName(resultSet.getString("fullname"));
                attendance.setStudent(student);
                attendance.setAttendance_date(resultSet.getDate("attendance_date"));
                attendance.setStatus(resultSet.getInt("status"));
                attendanceList.add(attendance);
            }

            if (!attendanceList.isEmpty()) {
                for (Attendance attendance1 : attendanceList) {
                    System.out.println(attendance1.getStudent().getName() + "\t" + attendance1.getAttendance_date()
                            + "\t" + (attendance1.getStatus() == 0 ? "Có mặt" : "Vắng mặt"));
                }
            } else System.out.println("Không có dữ liệu điểm danh");
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình lấy dữ liệu điểm danh.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình lấy dữ liệu điểm danh.");
            }
        }
    }

    private static void getStudents(Connect conn, PreparedStatement preparedStatement,
                                    ResultSet resultSet) {
        try {
            conn.openConnect();
            String sqlQuery = "select * from tbl_students";
            if (studentList == null) {
                studentList = new ArrayList<>();

                Student student = null;
                preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    student = new Student();
                    student.setId(resultSet.getInt(1));
                    student.setName(resultSet.getString(2));
                    studentList.add(student);
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("Có lỗi trong quá trình lấy dữ liệu học viên.");
        } finally {
            try {
                if (conn != null) conn.closeConnect();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException sqlException) {
                System.out.println("Có lỗi trong quá trình lấy dữ liệu học viên.");
            }
        }
    }
}
