package fu.se123456;

import fu.se123456.dao.EmployeeDAO;
import fu.se123456.dao.ProjectDAO;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   HSF302 - Chapter 1: JPA Mapping ManyToMany (Slot 4)           ");
        System.out.println("==================================================================");

        EmployeeDAO employeeDAO = new EmployeeDAO();
        ProjectDAO projectDAO = new ProjectDAO();

        // -------------------------------------------------------------------------
        // TODO 5.4 — KIEM CHUNG equals() / hashCode() VOI BUSINESS KEY
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.4: Kiem chung equals()/hashCode() voi Set ---");
        Set<Employee> empSet = new HashSet<>();
        Employee testEmp1 = new Employee("same.email@company.com", "Nguyen Van Mot", Gender.MALE,
                new BigDecimal("10000000"), LocalDate.now());
        Employee testEmp2 = new Employee("same.email@company.com", "Nguyen Van Hai (Trung Email)", Gender.FEMALE,
                new BigDecimal("12000000"), LocalDate.now());

        empSet.add(testEmp1);
        empSet.add(testEmp2);
        System.out.println("So luong phan tu trong Set (cung email, khac reference): " + empSet.size());
        System.out.println("=> KET QUA: " + (empSet.size() == 1 ? "DUNG (Set chi giu 1 phan tu duy nhat nho equals/hashCode email!)" : "SAI"));

        // -------------------------------------------------------------------------
        // TODO 5.7 — TAO 3 EMPLOYEE, 2 PROJECT & PHAN CONG CHEO
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.7: Tao 3 Employee, 2 Project & Phan cong cheo ---");

        // 1) Tạo hoặc lấy 3 Employee
        Employee nv1 = employeeDAO.findByEmail("nv1@company.com");
        if (nv1 == null) {
            nv1 = new Employee("nv1@company.com", "Nguyen Van An", Gender.MALE,
                    new BigDecimal("20000000"), LocalDate.of(2022, 1, 15), true);
            employeeDAO.save(nv1);
        }

        Employee nv2 = employeeDAO.findByEmail("nv2@company.com");
        if (nv2 == null) {
            nv2 = new Employee("nv2@company.com", "Tran Thi Binh", Gender.FEMALE,
                    new BigDecimal("25000000"), LocalDate.of(2021, 5, 20), true);
            employeeDAO.save(nv2);
        }

        Employee nv3 = employeeDAO.findByEmail("nv3@company.com");
        if (nv3 == null) {
            nv3 = new Employee("nv3@company.com", "Le Van Cuong", Gender.OTHER,
                    new BigDecimal("18000000"), LocalDate.of(2023, 8, 10), true);
            employeeDAO.save(nv3);
        } else {
            // Đảm bảo nv3 active lại nếu chạy lại nhiều lần
            nv3.setActive(true);
            employeeDAO.update(nv3);
        }

        // 2) Tạo hoặc lấy 2 Project
        Project projA = projectDAO.findByProjectCode("PRJ_A");
        if (projA == null) {
            projA = new Project("PRJ_A", "Du an AI Chatbot",
                    new BigDecimal("500000000"), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            projectDAO.save(projA);
        }

        Project projB = projectDAO.findByProjectCode("PRJ_B");
        if (projB == null) {
            projB = new Project("PRJ_B", "Du an Cloud Migration",
                    new BigDecimal("800000000"), LocalDate.of(2024, 3, 1), null);
            projectDAO.save(projB);
        }

        // 3) Phân công chéo:
        // NV1 tham gia Project A + B
        // NV2 tham gia Project B
        // NV3 tham gia Project A
        employeeDAO.assignEmployeeToProject(nv1.getId(), projA.getId());
        employeeDAO.assignEmployeeToProject(nv1.getId(), projB.getId());
        employeeDAO.assignEmployeeToProject(nv2.getId(), projB.getId());
        employeeDAO.assignEmployeeToProject(nv3.getId(), projA.getId());

        System.out.println("=> Da phan cong cheo thanh cong!");

        // In ra danh sách project của từng nhân viên
        List<Employee> allEmployees = employeeDAO.findAllWithProjects();
        System.out.println("\nDanh sach du an cua tung nhan vien:");
        for (Employee emp : allEmployees) {
            System.out.println("- Nhan vien: " + emp.getFullName() + " (" + emp.getEmail() + ")");
            for (Project p : emp.getProjects()) {
                System.out.println("   + Du an: " + p.getProjectName() + " [" + p.getProjectCode() + "]");
            }
        }

        // -------------------------------------------------------------------------
        // TODO 5.8 — JPQL DEM SO NHAN VIEN ACTIVE VA TONG SALARY THEO PROJECT
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.8: Thong ke so nhan vien active va tong luong moi Project ---");
        List<Object[]> stats = projectDAO.getProjectEmployeeStats();
        for (Object[] row : stats) {
            String pName = (String) row[0];
            Long empCount = (Long) row[1];
            BigDecimal totalSalary = (BigDecimal) row[2];
            System.out.println("Project: " + pName + " | So NV active: " + empCount + " | Tong luong: " + totalSalary);
        }

        // -------------------------------------------------------------------------
        // TODO 5.10 — JPQL TIM EMPLOYEE THAM GIA > 1 PROJECT CUNG LUC
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.10: Tim cac Employee active tham gia > 1 Project ---");
        List<Employee> multiProjEmps = employeeDAO.findActiveEmployeesWithMultipleProjects();
        System.out.println("So luong nhan vien tham gia > 1 du an: " + multiProjEmps.size());
        for (Employee e : multiProjEmps) {
            System.out.println("- " + e.getFullName() + " (" + e.getEmail() + ") dang tham gia "
                    + e.getProjects().size() + " du an.");
        }

        // -------------------------------------------------------------------------
        // TODO 5.9 — GO NHAN VIEN KHOI DU AN (UNASSIGN)
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.9: Go NV1 khoi Project A (unassign) ---");
        System.out.println("So du an cua NV1 truoc khi go: " + employeeDAO.findByIdWithProjects(nv1.getId()).getProjects().size());
        employeeDAO.unassignEmployeeFromProject(nv1.getId(), projA.getId());

        Employee nv1After = employeeDAO.findByIdWithProjects(nv1.getId());
        System.out.println("So du an cua NV1 sau khi go khoi Project A: " + nv1After.getProjects().size());
        System.out.println("Kiem tra Employee goc: ID " + nv1After.getId() + " van ton tai binh thuong.");
        System.out.println("Kiem tra Project goc: ID " + projA.getId() + " van ton tai binh thuong.");
        System.out.println("=> Bang trung gian employee_project chi mat dung 1 lien ket (nv1, projA).");

        // -------------------------------------------------------------------------
        // TODO 5.11 — DEACTIVATE EMPLOYEE (SET ACTIVE = FALSE)
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 5.11: Deactivate NV3 (Nghi viec) ---");
        employeeDAO.deactivateEmployee(nv3.getId());
        Employee nv3Deactivated = employeeDAO.findByIdWithProjects(nv3.getId());
        System.out.println("Trang thai active cua NV3 sau deactivate: " + nv3Deactivated.isActive());
        System.out.println("Lich su tham gia du an cua NV3 van con nguyen ven: " + nv3Deactivated.getProjects().size() + " du an.");
        System.out.println("=> KHONG cascade REMOVE de luu vet lich su cong tac!");

        // -------------------------------------------------------------------------
        // DONG TAI NGUYEN
        // -------------------------------------------------------------------------
        System.out.println("\n==================================================================");
        System.out.println("     HOAN THANH TAT CA CAC YEU CAU THUC HANH SLOT 4 (M2M)!       ");
        System.out.println("==================================================================");
        JPAUtil.close();
    }
}