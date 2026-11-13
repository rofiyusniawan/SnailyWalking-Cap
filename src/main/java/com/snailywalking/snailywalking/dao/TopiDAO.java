
package com.snailywalking.snailywalking.dao;

import com.snailywalking.snailywalking.HibernateUtil;
import com.snailywalking.snailywalking.model.TopiEntity;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TopiDAO {

    // Menampilkan semua data topi
    public List<TopiEntity> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM TopiEntity", TopiEntity.class
            ).list();
        }
    }

    // Mencari topi berdasarkan kode
    public TopiEntity getByKode(String kode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TopiEntity.class, kode);
        }
    }

    // Menambahkan data topi
    public void save(TopiEntity topi) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(topi);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Mengubah data topi
    public void update(TopiEntity topi) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(topi);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Menghapus data topi
    public void delete(String kode) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            TopiEntity topi = session.get(TopiEntity.class, kode);

            if (topi != null) {
                session.remove(topi);
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
