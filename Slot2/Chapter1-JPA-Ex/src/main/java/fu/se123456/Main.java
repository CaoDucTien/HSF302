package fu.se123456;

import fe.masv.dao.EmployeeDAO;
import fe.masv.pojo.Employee;
import fe.masv.pojo.Gender;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hsf302FU");
        System.out.println("EMF tao thanh cong!");

        EmployeeDAO dao = new EmployeeDAO(emf);

        System.out.println("\n==========================================");
        System.out.println("   BẮT ĐẦU DEMO LUỒNG CRUD (TODO 0.8)     ");
        System.out.println("==========================================");

        // ----------------------------------------------------
        // 1. CREATE
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 1: CREATE (Thêm mới nhân viên) ---");
        // [LIFECYCLE - TODO 0.10]: newEmp đang ở trạng thái NEW / TRANSIENT (vừa tạo bằng 'new', chưa có ID, chưa được Persistence Context quản lý)
        Employee newEmp = new Employee();
        newEmp.setFullName("Nguyen Van An");
        newEmp.setEmail("an.nguyen" + System.currentTimeMillis() + "@fe.edu.vn");
        newEmp.setSalary(new BigDecimal("1500.00"));
        newEmp.setGender(Gender.MALE);
        newEmp.setHireDate(LocalDate.of(2021, 5, 20));
        newEmp.setActive(true);

        System.out.println("ID trước khi save: " + newEmp.getId());
        // [LIFECYCLE - TODO 0.10]: Trong dao.save(): ngay sau em.persist(e) (trong transaction) -> entity chuyển sang MANAGED.
        // [LIFECYCLE - TODO 0.10]: Sau khi method save() return (EntityManager đã đóng): entity chuyển sang trạng thái DETACHED.
        dao.save(newEmp);
        Long empId = newEmp.getId();
        System.out.println("Đã lưu nhân viên thành công! ID sinh tự động: " + empId);

        // ----------------------------------------------------
        // 2. READ (TODO 0.4 & TODO 0.5)
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 2: READ (TODO 0.4: findById, findAll & TODO 0.5: JPQL có điều kiện) ---");
        // [LIFECYCLE - TODO 0.10]: Trong dao.findById(): em.find() trả về entity ở trạng thái MANAGED. Sau khi findById() return (em đã close()), foundEmp ở trạng thái DETACHED.
        Employee foundEmp = dao.findById(empId);
        if (foundEmp != null) {
            System.out.println("2.1. [TODO 0.4] findById(" + empId + "):");
            System.out.println(" - Họ và tên: " + foundEmp.getFullName());
            System.out.println(" - Email: " + foundEmp.getEmail());
            System.out.println(" - Lương: " + foundEmp.getSalary());
            System.out.println(" - Giới tính: " + foundEmp.getGender());
            System.out.println(" - Ngày vào làm: " + foundEmp.getHireDate());
            System.out.println(" - Số năm cống hiến (@Transient): " + foundEmp.getYearsOfService() + " năm");
            System.out.println(" - Active: " + foundEmp.isActive());
        }

        // [TODO 0.4]: findAll()
        System.out.println("2.2. [TODO 0.4] findAll(): Tổng số nhân viên hiện tại = " + dao.findAll().size());

        // [TODO 0.5]: findByEmail
        Employee byEmail = dao.findByEmail(newEmp.getEmail());
        System.out.println("2.3. [TODO 0.5] findByEmail('" + newEmp.getEmail() + "'): " + (byEmail != null ? byEmail.getFullName() : "null"));

        // [TODO 0.5]: findBySalaryGreaterThan
        System.out.println("2.4. [TODO 0.5] findBySalaryGreaterThan(1000): " + dao.findBySalaryGreaterThan(new BigDecimal("1000.00")).size() + " nhân viên");

        // [TODO 0.5]: findByActive
        System.out.println("2.5. [TODO 0.5] findByActive(true): " + dao.findByActive(true).size() + " nhân viên");

        // ----------------------------------------------------
        // 3. UPDATE
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 3: UPDATE (Cập nhật thông tin) ---");
        // [LIFECYCLE - TODO 0.10]: foundEmp đang ở trạng thái DETACHED, sửa field lúc này chưa tự động đồng bộ vào DB
        foundEmp.setSalary(new BigDecimal("2350.75"));
        foundEmp.setActive(false);
        // [LIFECYCLE - TODO 0.10]: Khi gọi dao.update(foundEmp): object trả về từ em.merge() là MANAGED (trong transaction đó), object cũ truyền vào vẫn DETACHED.
        dao.update(foundEmp);
        System.out.println("Đã gọi dao.update() để cập nhật lương = 2350.75 và active = false");

        // ----------------------------------------------------
        // 4. READ LẠI KIỂM TRA UPDATE
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 4: READ LẠI (Kiểm tra cập nhật) ---");
        // [LIFECYCLE - TODO 0.10]: updatedEmp sau khi lấy ra từ findById() và đóng EM là DETACHED với dữ liệu mới nhất từ DB
        Employee updatedEmp = dao.findById(empId);
        if (updatedEmp != null) {
            System.out.println("Thông tin sau update:");
            System.out.println(" - Lương mới: " + updatedEmp.getSalary());
            System.out.println(" - Trạng thái Active mới: " + updatedEmp.isActive());
        }

        // ----------------------------------------------------
        // 5. DELETE
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 5: DELETE (Xóa nhân viên) ---");
        // [LIFECYCLE - TODO 0.10]: Trong dao.delete(): em.find() lấy entity thành MANAGED, sau đó em.remove() chuyển entity sang trạng thái REMOVED trong transaction. Sau khi commit, bản ghi biến mất khỏi DB.
        boolean isDeleted = dao.delete(empId);
        System.out.println("Kết quả xóa nhân viên ID " + empId + ": " + isDeleted);

        // ----------------------------------------------------
        // 6. READ LẠI KIỂM TRA ĐÃ XÓA
        // ----------------------------------------------------
        System.out.println("\n--- BƯỚC 6: READ LẠI (Kiểm tra sau xóa) ---");
        // [LIFECYCLE - TODO 0.10]: Do entity đã bị xóa khỏi DB, em.find() trả về null
        Employee deletedCheck = dao.findById(empId);
        if (deletedCheck == null) {
            System.out.println("Kết quả: null -> Nhân viên ID " + empId + " không tìm thấy (Đã xóa thành công khỏi DB!)");
        } else {
            System.out.println("LỖI: Nhân viên vẫn còn tồn tại: " + deletedCheck);
        }

        System.out.println("\n==========================================");
        System.out.println("       HOÀN THÀNH DEMO CRUD THÀNH CÔNG    ");
        System.out.println("==========================================");

        // ----------------------------------------------------
        // TODO 0.9 — KIỂM CHỨNG RÀNG BUỘC UNIQUE TRÊN EMAIL
        // ----------------------------------------------------
        System.out.println("\n==========================================");
        System.out.println("  KIỂM CHỨNG RÀNG BUỘC UNIQUE (TODO 0.9)  ");
        System.out.println("==========================================");

        String duplicateEmail = "test.unique." + System.currentTimeMillis() + "@fe.edu.vn";

        // 1. Tạo nhân viên thứ nhất
        Employee emp1 = new Employee();
        emp1.setFullName("Nguyen Van Mot");
        emp1.setEmail(duplicateEmail);
        emp1.setSalary(new BigDecimal("1000.00"));
        emp1.setGender(Gender.MALE);
        emp1.setHireDate(LocalDate.now());
        emp1.setActive(true);
        dao.save(emp1);
        System.out.println("Tạo nhân viên 1 thành công với email: " + duplicateEmail);

        // 2. Cố ý tạo nhân viên thứ hai trùng email
        Employee emp2 = new Employee();
        emp2.setFullName("Tran Thi Hai");
        emp2.setEmail(duplicateEmail);
        emp2.setSalary(new BigDecimal("1200.00"));
        emp2.setGender(Gender.FEMALE);
        emp2.setHireDate(LocalDate.now());
        emp2.setActive(true);

        try {
            System.out.println("Đang cố ý lưu nhân viên 2 với cùng email: " + duplicateEmail + "...");
            dao.save(emp2);
            System.out.println("LỖI: Ràng buộc unique không hoạt động!");
        } catch (Exception ex) {
            System.out.println("=> Bắt ngoại lệ thành công (Ràng buộc Unique hoạt động chuẩn xác!):");
            System.out.println("   Loại Exception: " + ex.getClass().getName());
            System.out.println("   Nguyên nhân gốc (Root Cause): " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
        } finally {
            // Dọn dẹp nhân viên 1 sau khi kiểm chứng
            dao.delete(emp1.getId());
            System.out.println("Đã dọn dẹp nhân viên 1 sau khi kiểm chứng.");
        }

        System.out.println("\n==========================================");
        System.out.println("       HOÀN THÀNH TẤT CẢ CÁC BƯỚC DEMO    ");
        System.out.println("==========================================");

        // Đóng tài nguyên EntityManagerFactory
        emf.close();
    }
}