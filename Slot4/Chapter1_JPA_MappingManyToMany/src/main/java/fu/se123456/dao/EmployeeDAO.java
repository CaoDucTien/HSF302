package fu.se123456.dao;

import fu.se123456.pojo.Employee;
import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class EmployeeDAO {

    private final EntityManagerFactory emf;

    public EmployeeDAO() {
        this.emf = JPAUtil.getEMF();
    }

    public EmployeeDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

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
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

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

    public Employee findByIdWithProjects(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e LEFT JOIN FETCH e.projects WHERE e.id = :id",
                    Employee.class
            ).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

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

    public List<Employee> findAllWithProjects() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.projects",
                    Employee.class
            ).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

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

    public Employee update(Employee e) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee managedEmp = em.merge(e);
            tx.commit();
            return managedEmp;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee e = em.find(Employee.class, id);
            if (e != null) {
                em.remove(e);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 5.6 â€” Viáº¿t EmployeeDAO vá»›i method assignEmployeeToProject(Long employeeId, Long projectId):
    // find cáº£ 2 entity trong 1 transaction rá»“i gá»i assignToProject().
    public void assignEmployeeToProject(Long employeeId, Long projectId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee emp = em.find(Employee.class, employeeId);
            Project proj = em.find(Project.class, projectId);
            if (emp != null && proj != null) {
                emp.assignToProject(proj);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 5.9 â€” unassignEmployeeFromProject(Long employeeId, Long projectId):
    // Gá»¡ nhÃ¢n viÃªn khá»i dá»± Ã¡n trong 1 transaction
    public void unassignEmployeeFromProject(Long employeeId, Long projectId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee emp = em.find(Employee.class, employeeId);
            Project proj = em.find(Project.class, projectId);
            if (emp != null && proj != null) {
                emp.unassignFromProject(proj);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 5.10 â€” Viáº¿t JPQL tÃ¬m cÃ¡c Employee (chá»‰ láº¥y active = true) Ä‘ang tham gia nhiá»u hÆ¡n 1 project cÃ¹ng lÃºc:
    // SELECT e FROM Employee e WHERE e.active = true AND SIZE(e.projects) > 1
    public List<Employee> findActiveEmployeesWithMultipleProjects() {
        EntityManager em = emf.createEntityManager();
        try {
            // Dung LEFT JOIN FETCH e.projects de load luon danh sach du an, tranh LazyInitializationException sau khi dong EntityManager
            String jpql = "SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.projects WHERE e.active = true AND SIZE(e.projects) > 1";
            return em.createQuery(jpql, Employee.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 5.11 â€” Viáº¿t method deactivateEmployee(Long employeeId) (set active = false):
    // Giáº£i thÃ­ch trong comment:
    // Khi má»™t nhÃ¢n viÃªn nghá»‰ viá»‡c (deactivate), há»‡ thá»‘ng CHá»ˆ NÃŠN set active = false,
    // KHÃ”NG NÃŠN tá»± Ä‘á»™ng gá»¡ nhÃ¢n viÃªn Ä‘Ã³ ra khá»i táº¥t cáº£ cÃ¡c project trong báº£ng employee_project.
    // LÃ½ do: Cáº§n giá»¯ láº¡i lá»‹ch sá»­ Ä‘Ã³ng gÃ³p (audit trail / work history) cá»§a nhÃ¢n viÃªn cho cÃ¡c dá»± Ã¡n trong quÃ¡ khá»©.
    // Náº¿u gá»¡ liÃªn káº¿t hoáº·c dÃ¹ng cascade REMOVE, bÃ¡o cÃ¡o nhÃ¢n sá»± vÃ  chi phÃ­ cá»§a dá»± Ã¡n sáº½ bá»‹ sai lá»‡ch.
    // VÃ¬ váº­y, trong quan há»‡ Many-to-Many tuyá»‡t Ä‘á»‘i KHÃ”NG dÃ¹ng cascade = CascadeType.REMOVE.
    public void deactivateEmployee(Long employeeId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee emp = em.find(Employee.class, employeeId);
            if (emp != null) {
                emp.setActive(false);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}