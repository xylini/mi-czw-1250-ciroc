package pl.edu.agh.timekeeper.db.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

public abstract class DaoBase<T> {

    private final Class<T> classParameter;

    private final String tableName;

    public DaoBase(Class<T> classParameter, String tableName) {
        this.classParameter = classParameter;
        this.tableName = tableName;
    }

    public Optional<T> getById(int id) throws PersistenceException {
        try {
            return Optional.of(getByIdBase(id));
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<List<T>> getAll() throws PersistenceException {
        try {
            List<T> l = SessionService.getCurrentSession().createQuery("from " + tableName).list();
            return Optional.of(l);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<T> create(T entity) {
        try {
            final Session session = SessionService.getCurrentSession();
            final Transaction tx = session.beginTransaction();
            int id = (int) session.save(entity);
            session.merge(entity);
            tx.commit();
            return Optional.of(getByIdBase(id));
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean update(T entity) {
        try {
            final Session session = SessionService.getCurrentSession();
            final Transaction tx = session.beginTransaction();
            session.update(entity);
            session.merge(entity);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(T entity) {
        try {
            final Session session = SessionService.getCurrentSession();
            final Transaction tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteById(int id) throws PersistenceException {
        delete(getByIdBase(id));
    }

    private T getByIdBase(int id) throws PersistenceException {
        return SessionService.getCurrentSession()
                .createQuery(String.format("SELECT t FROM %s t WHERE t.id = :id", tableName), classParameter)
                .setParameter("id", id).getSingleResult();
    }
}
