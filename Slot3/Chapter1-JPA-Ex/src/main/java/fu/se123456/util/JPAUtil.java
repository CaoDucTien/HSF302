package fu.se123456.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("hsf302FU");

    private JPAUtil() {
    }

    public static EntityManagerFactory getEMF() {
        return EMF;
    }

    // Alias tiện ích
    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF;
    }

    public static void close() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}