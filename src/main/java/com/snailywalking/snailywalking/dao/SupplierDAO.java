
package com.snailywalking.snailywalking.dao;

import com.snailywalking.snailywalking.HibernateUtil;
import com.snailywalking.snailywalking.model.SupplierEntity;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierDAO {

    // Menampilkan semua data supplier
    public List<SupplierEntity> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM SupplierEntity", SupplierEntity.class
            ).list();
        }
    }

    // Mencari supplier berdasarkan kode
    public SupplierEntity getByKode(String kode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(SupplierEntity.class, kode);
        }
    }

    // Menambahkan supplier
    public void save(SupplierEntity supplier) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(supplier);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Mengubah supplier
    public void update(SupplierEntity supplier) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(supplier);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Menghapus supplier
    public void delete(String kode) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            SupplierEntity supplier = session.get(SupplierEntity.class, kode);

            if (supplier != null) {
                session.remove(supplier);
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
