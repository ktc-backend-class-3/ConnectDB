DROP TABLE tbl_students;
CREATE TABLE tbl_students (
  id number GENERATED AS IDENTITY PRIMARY KEY,
  fullname varchar2(50)
);

DROP TABLE tbl_attendance;
CREATE TABLE tbl_attendance (
  id int GENERATED AS IDENTITY PRIMARY KEY,
  student_id int,
  attendance_date timestamp,
  status int
);

DROP TABLE tbl_users;
CREATE TABLE tbl_users (
  id int GENERATED AS IDENTITY PRIMARY KEY,
  username varchar2(50),
  password varchar2(50),
  role_id int
);

DROP TABLE tbl_roles;
CREATE TABLE tbl_roles (
  id int GENERATED AS IDENTITY PRIMARY KEY,
  role_name varchar2(50)
);

DROP TABLE tbl_permissions;
CREATE TABLE tbl_permissions (
  id int GENERATED AS IDENTITY PRIMARY KEY,
  permission_name varchar2(50)
);

DROP TABLE tbl_roles_permission;
CREATE TABLE tbl_roles_permission (
  role_id int NOT NULL,
  permission_id int NOT NULL
);