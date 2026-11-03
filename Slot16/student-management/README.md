# Student Management System (JavaFX + Spring Data JPA)

## 1. Yêu cầu môi trường
- JDK 21, Maven 3.9+
- Spring Boot 4.x / 3.5.x, JavaFX 21.0.5
- SQL Server (TCP/IP port 1433 bật, tài khoản `sa`)
- IntelliJ IDEA, Gluon Scene Builder (tùy chọn)

## 2. Cài đặt CSDL (SQL Server)
1. Mở SSMS và tạo database:
   ```sql
   IF DB_ID('StudentDB') IS NULL CREATE DATABASE StudentDB;
   ```
2. Cấu hình kết nối trong file `ui-module/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=false;trustServerCertificate=true
   spring.datasource.username=sa
   spring.datasource.password=12345
   ```
3. Chèn dữ liệu mẫu (tùy chọn):
   ```sql
   USE StudentDB;
   INSERT INTO students(name, email, age) VALUES
   (N'Nguyễn Văn An', 'an.nv@fpt.edu.vn',   20),
   (N'Trần Thị Bình', 'binh.tt@fpt.edu.vn', 21),
   (N'Lê Văn Cường',  'cuong.lv@gmail.com', 22),
   (N'Phạm Thị Dung', 'dung.pt@fpt.edu.vn', 19),
   (N'Hoàng Văn Em',  'em.hv@gmail.com',    23);
   ```

## 3. Build & Test
Chạy kiểm thử tự động (sử dụng H2 in-memory DB, không phụ thuộc SQL Server):
```bash
mvn clean install
```
Kết quả kiểm thử:
- `StudentRepositoryTest`: 10/10 tests pass.
- `StudentServiceImplTest`: 12 methods (16 invocations) pass.

## 4. Chạy ứng dụng
* **Qua IntelliJ IDEA:** Chạy class `com.example.ui.Launcher` (trong `ui-module`).
* **Qua Terminal:**
  ```bash
  mvn -q clean install -DskipTests
  mvn -f ui-module/pom.xml spring-boot:run
  ```

## 5. Kiến trúc N-Layer
`StudentController` → `StudentService` (interface) → `StudentServiceImpl` → `StudentRepository` (`JpaRepository`) → SQL Server (hoặc H2 khi test).

