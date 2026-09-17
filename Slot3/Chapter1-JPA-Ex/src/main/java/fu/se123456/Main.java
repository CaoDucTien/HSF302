package fu.se123456;

import fu.se123456.dao.DepartmentDAO;
import fu.se123456.dao.EmployeeDAO;
import fu.se123456.pojo.Department;
import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Gender;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  HSF302 - Thuc hanh 2: CRUD JPA Mapping (OneToMany / ManyToOne)  ");
        System.out.println("==================================================================");

        DepartmentDAO departmentDAO = new DepartmentDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        // -------------------------------------------------------------------------
        // TODO 2.4 — KIEM CHUNG HELPER METHOD DONG BO 2 CHIEU
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 2.4: Kiem chung Helper Method dong bo 2 chieu ---");
        Department dept = new Department("IT", "Ha Noi");
        Employee emp = new Employee("test@company.com", "Test", Gender.OTHER,
                new BigDecimal("1000"), LocalDate.now());
        dept.addEmployee(emp);
        System.out.println(dept.getEmployees().contains(emp)); // phải true
        System.out.println(emp.getDepartment() == dept); // phải true

        // -------------------------------------------------------------------------
        // TODO 2.7 — TAO DEPARTMENT VA 3 EMPLOYEE, PERSIST DEPARTMENT (CASCADE = ALL)
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 2.7: Demo Tao Department + 3 Employee & Cascade Persist ---");
        // 1) Tạo Department + 3 Employee, add qua helper method (TODO 2.4)
        Department it = new Department("Marketing", "Ha Noi");

        Employee e1 = new Employee("aa.nguyen@company.com", "Nguyen Van A", Gender.MALE,
                new BigDecimal("15000000"), LocalDate.of(2022, 1, 10));
        Employee e2 = new Employee("bb.tran@company.com", "Tran Thi B", Gender.FEMALE,
                new BigDecimal("18000000"), LocalDate.of(2021, 6, 1));
        Employee e3 = new Employee("cc.le@company.com", "Le Van C", Gender.OTHER,
                new BigDecimal("12000000"), LocalDate.of(2023, 3, 15));

        it.addEmployee(e1);
        it.addEmployee(e2);
        it.addEmployee(e3);

        // 2) Chỉ persist(department) — cascade = ALL tự lo phần Employee (TODO 2.7)
        departmentDAO.save(it);
        System.out.println("Da luu Department, id = " + it.getId());

        // 3) Tim lai kem employees bang JOIN FETCH (TODO 2.6) — khong bi
        //    LazyInitializationException du EntityManager cua lan tim nay da dong,
        //    vi employees da duoc load ngay trong cung 1 query.
        Department found = departmentDAO.findByIdWithEmployees(it.getId());
        System.out.println("Phong ban: " + found.getName());
        for (Employee e : found.getEmployees()) {
            System.out.println("  - " + e);
        }

        // -------------------------------------------------------------------------
        // TODO 2.8 — TAI HIEN N+1 QUERY PROBLEM
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 2.8: Tai hien N+1 Query Problem ---");
        System.out.println("Goi findAll() cac Department (khong JOIN FETCH), sau do loop qua getEmployees():");
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            // 1 cau query SELECT tat ca Department (Cau 1)
            List<Department> deptList = em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
            System.out.println("So luong Department: " + deptList.size());
            for (Department d : deptList) {
                // Vi employees la LAZY, moi lan truy cap d.getEmployees().size() se sinh ra 1 cau SELECT rieng
                // Tong cong sinh ra: 1 cau SELECT Department + N cau SELECT Employees = (1 + N) cau SQL!
                System.out.println("  - Department: " + d.getName() + " (ID: " + d.getId() + ") co "
                        + d.getEmployees().size() + " nhan vien.");
            }
        } finally {
            em.close();
        }

        // -------------------------------------------------------------------------
        // TODO 2.9 — FIX N+1 BANG JPQL JOIN FETCH
        // -------------------------------------------------------------------------
        System.out.println("\n--- TODO 2.9: Fix N+1 bang JOIN FETCH (findAllWithEmployees) ---");
        // Chi ton dung 1 cau SELECT duy nhat (co JOIN) de lay toan bo Department + Employees
        List<Department> deptWithEmps = departmentDAO.findAllWithEmployees();
        System.out.println("So luong Department (voi JOIN FETCH): " + deptWithEmps.size());
        for (Department d : deptWithEmps) {
            System.out.println("  - Department: " + d.getName() + " co " + d.getEmployees().size() + " nhan vien.");
        }
        System.out.println("=> SO SANH: TODO 2.8 sinh ra (1 + N) cau SQL vs TODO 2.9 chi sinh dung 1 cau SQL!");

        // -------------------------------------------------------------------------
        // KIEM TRA RANG BUOC UNIQUE TREN EMAIL (Checklist)
        // -------------------------------------------------------------------------
        System.out.println("\n--- Kiem tra rang buoc Unique tren Email ---");
        Employee dupEmp = new Employee("aa.nguyen@company.com", "Nguyen Van Trung", Gender.MALE,
                new BigDecimal("10000000"), LocalDate.now());
        dupEmp.setDepartment(it);

        try {
            System.out.println("Thu luu nhan vien voi email da ton tai: " + dupEmp.getEmail());
            employeeDAO.save(dupEmp);
            System.out.println("LOI: Rang buoc Unique khong hoat dong!");
        } catch (Exception ex) {
            System.out.println("=> Bat ngoai le thanh cong (Unique Email hoat dong chuan xac!): " + ex.getMessage());
        }

        // -------------------------------------------------------------------------
        // KIEM TRA CASCADE DELETE / ORPHAN REMOVAL (Checklist)
        // -------------------------------------------------------------------------
        System.out.println("\n--- Kiem tra Cascade Delete / Orphan Removal ---");
        Department tempDept = new Department("Temp Department", "Da Nang");
        Employee tempEmp = new Employee("temp.emp@company.com", "Temp Staff", Gender.OTHER,
                new BigDecimal("9000000"), LocalDate.now());
        tempDept.addEmployee(tempEmp);
        departmentDAO.save(tempDept);
        Long tempId = tempDept.getId();
        System.out.println("Da tao phong ban tam thoi ID = " + tempId + " voi 1 nhan vien (temp.emp@company.com)");

        // Xoa phong ban tam thoi
        departmentDAO.delete(tempId);
        System.out.println("Da xoa phong ban ID = " + tempId);

        // Kiem tra lai Employee cua phong ban da bi xoa chua
        Employee checkDeleted = employeeDAO.findByEmail("temp.emp@company.com");
        System.out.println("Kiem tra nhan vien temp.emp@company.com trong DB: "
                + (checkDeleted == null ? "null (Da bi xoa dong thoi nho cascade = ALL / orphanRemoval!)" : "Van ton tai (LOI)"));

        // -------------------------------------------------------------------------
        // DONG TAI NGUYEN
        // -------------------------------------------------------------------------
        System.out.println("\n==================================================================");
        System.out.println("        HOAN THANH TAT CA CAC YEU CAU THUC HANH SLOT 3!           ");
        System.out.println("==================================================================");
        JPAUtil.close();
    }
}