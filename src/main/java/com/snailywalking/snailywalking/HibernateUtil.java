package com.snailywalking.snailywalking;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(TopiEntity.class)
                    .addAnnotatedClass(SupplierEntity.class)
                    .addAnnotatedClass(PembelianEntity.class)
                    .addAnnotatedClass(PenjualanEntity.class)
                    .buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Gagal membuat SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}