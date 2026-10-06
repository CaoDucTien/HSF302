package fe.masv.dao;

import fe.masv.pojo.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.List;

public class EmployeeDAO {
    private EntityManagerFactory emf;

    public EmployeeDAO() {
        this.emf = Persistence.createEntityManagerFactory("hsf302FU");
    }

    public EmployeeDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // TODO 0.3 — CREATE: EmployeeDAO.save(Employee e)
    public void save(Employee e) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(e);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex; // Ném ra ngoại lệ để phía gọi (Main) có thể bắt và kiểm chứng ràng buộc
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.4 — READ: findById
    public Employee findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.4 — READ: findAll
    public List<Employee> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.5 — READ có điều kiện: Tìm nhân viên theo email
    public Employee findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.5 — READ có điều kiện: Tìm danh sách nhân viên có salary lớn hơn mức chỉ định
    public List<Employee> findBySalaryGreaterThan(BigDecimal minSalary) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.salary > :minSalary", Employee.class)
                    .setParameter("minSalary", minSalary)
                    .getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.5 — READ có điều kiện: Tìm danh sách nhân viên theo trạng thái active
    public List<Employee> findByActive(boolean active) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.active = :active", Employee.class)
                    .setParameter("active", active)
                    .getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.6 — UPDATE: EmployeeDAO.update(Employee e)
    public Employee update(Employee e) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            e = em.merge(e); // merge trả về managed copy của entity
            tx.commit();
            return e;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            ex.printStackTrace();
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 0.7 — DELETE: EmployeeDAO.delete(Long id)
    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee e = em.find(Employee.class, id); // Tìm entity trước khi xóa
            if (e != null) {
                em.remove(e); // Chỉ remove khi entity khác null và đang ở trạng thái Managed
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
