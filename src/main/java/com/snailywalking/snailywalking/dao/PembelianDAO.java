
package com.snailywalking.snailywalking.dao;

import com.snailywalking.snailywalking.HibernateUtil;
import com.snailywalking.snailywalking.model.PembelianEntity;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class PembelianDAO {

    // Menampilkan semua data pembelian
    public List<PembelianEntity> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM PembelianEntity ORDER BY tanggal DESC",
                    PembelianEntity.class
            ).list();
        }
    }

    // Mencari pembelian berdasarkan kode
    public PembelianEntity getByKode(String kode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PembelianEntity.class, kode);
        }
    }

    // Menambahkan pembelian
    public void save(PembelianEntity pembelian) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(pembelian);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Mengubah pembelian
    public void update(PembelianEntity pembelian) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(pembelian);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Menghapus pembelian
    public void delete(String kode) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            PembelianEntity pembelian = session.get(PembelianEntity.class, kode);

            if (pembelian != null) {
                session.remove(pembelian);
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
