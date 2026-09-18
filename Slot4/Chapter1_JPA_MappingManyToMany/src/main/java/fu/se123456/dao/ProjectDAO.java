package fu.se123456.dao;

import fu.se123456.pojo.Project;
import fu.se123456.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ProjectDAO {

    private final EntityManagerFactory emf;

    public ProjectDAO() {
        this.emf = JPAUtil.getEMF();
    }

    public ProjectDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(Project p) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(p);
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

    public Project findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Project findByIdWithEmployees(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Project p LEFT JOIN FETCH p.employees WHERE p.id = :id",
                    Project.class
            ).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Project findByProjectCode(String code) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p WHERE p.projectCode = :code", Project.class)
                    .setParameter("code", code)
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

    public List<Project> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Project update(Project p) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Project managedProject = em.merge(p);
            tx.commit();
            return managedProject;
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
            Project p = em.find(Project.class, id);
            if (p != null) {
                em.remove(p);
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

    // TODO 5.8 — Viết JPQL đếm số nhân viên active tham gia mỗi project và tính tổng salary của các nhân viên đó:
    // SELECT p.projectName, COUNT(e), SUM(e.salary) FROM Project p JOIN p.employees e WHERE e.active = true GROUP BY p.projectName
    public List<Object[]> getProjectEmployeeStats() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p.projectName, COUNT(e), SUM(e.salary) " +
                          "FROM Project p JOIN p.employees e " +
                          "WHERE e.active = true " +
                          "GROUP BY p.projectName";
            return em.createQuery(jpql, Object[].class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}