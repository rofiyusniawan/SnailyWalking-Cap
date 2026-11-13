
package com.snailywalking.snailywalking.dao;

import com.snailywalking.snailywalking.HibernateUtil;
import com.snailywalking.snailywalking.model.PenjualanEntity;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class PenjualanDAO {

    // Menampilkan semua data penjualan
    public List<PenjualanEntity> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM PenjualanEntity ORDER BY tanggal DESC",
                    PenjualanEntity.class
            ).list();
        }
    }

    // Mencari penjualan berdasarkan kode
    public PenjualanEntity getByKode(String kode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PenjualanEntity.class, kode);
        }
    }

    // Menambahkan data penjualan
    public void save(PenjualanEntity penjualan) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(penjualan);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Mengubah data penjualan
    public void update(PenjualanEntity penjualan) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(penjualan);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Menghapus data penjualan
    public void delete(String kode) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            PenjualanEntity penjualan =
                    session.get(PenjualanEntity.class, kode);

            if (penjualan != null) {
                session.remove(penjualan);
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
