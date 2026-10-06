package fu.se123456.dao;

import fu.se123456.pojo.Department;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class DepartmentDAO {

    private final EntityManagerFactory emf;

    public DepartmentDAO() {
        this.emf = JPAUtil.getEMF();
    }

    public DepartmentDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // TODO 2.5 — CREATE: save(Department d)
    public void save(Department d) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(d);
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

    // TODO 2.5 — READ: findById
    public Department findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 2.5 — READ: findAll (khong JOIN FETCH de demo N+1 o TODO 2.8)
    public List<Department> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 2.6 — JPQL JOIN FETCH: Tim 1 Department kem danh sach Employee
    public Department findByIdWithEmployees(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id",
                    Department.class
            ).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 2.9 — Fix N+1 bang JOIN FETCH: Lay tat ca Department kem danh sach Employee
    public List<Department> findAllWithEmployees() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees",
                    Department.class
            ).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // TODO 2.5 — UPDATE: update(Department d)
    public Department update(Department d) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Department managedDept = em.merge(d);
            tx.commit();
            return managedDept;
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

    // TODO 2.5 — DELETE: delete(Long id)
    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Department d = em.find(Department.class, id);
            if (d != null) {
                em.remove(d);
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
}