package com.snailywalking.snailywalking;

import com.snailywalking.snailywalking.model.PenjualanEntity;
import com.snailywalking.snailywalking.model.TopiEntity;
import com.snailywalking.snailywalking.model.PembelianEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataSeeder {

    public static void isiDataAwal() {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            // DATA TOPI
            if (session.createQuery(
                    "SELECT COUNT(t) FROM TopiEntity t", Long.class)
                    .getSingleResult() == 0) {

                session.persist(new TopiEntity(
                        "TP001", "New Era", "Baseball Cap",
                        "Hitam", "Baik", 80000, 150000, 1));

                session.persist(new TopiEntity(
                        "TP002", "Adidas", "Classic Cap",
                        "Biru", "Sangat Baik", 60000, 125000, 1));

                session.persist(new TopiEntity(
                        "TP003", "Nike", "Swoosh Cap",
                        "Merah", "Cukup Baik", 80000, 150000, 0));
            }

            // DATA PEMBELIAN
            if (session.createQuery(
                    "SELECT COUNT(p) FROM PembelianEntity p", Long.class)
                    .getSingleResult() == 0) {

                session.persist(new PembelianEntity(
                        "PB001", java.time.LocalDate.now().toString(),
                        "SP001", "TP001", 1, 80000));

                session.persist(new PembelianEntity(
                        "PB002", java.time.LocalDate.now().toString(),
                        "SP002", "TP002", 1, 60000));
            }

            // DATA PENJUALAN
            if (session.createQuery(
                    "SELECT COUNT(p) FROM PenjualanEntity p", Long.class)
                    .getSingleResult() == 0) {

                session.persist(new PenjualanEntity(
                        "PJ001", java.time.LocalDate.now().toString(),
                        "TP003", "Andi", 150000, "COD"));
            }

            transaction.commit();

            System.out.println("Data awal berhasil dimasukkan!");

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            System.out.println("Gagal memasukkan data awal!");
            e.printStackTrace();
        }
    }
}