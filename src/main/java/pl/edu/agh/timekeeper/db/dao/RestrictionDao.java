package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.model.Restriction;

import javax.persistence.PersistenceException;
import java.util.Optional;

public class RestrictionDao extends DaoBase<Restriction> {

    private static final String TABLE_NAME = Restriction.class.getName();

    public RestrictionDao() {
        super(Restriction.class, TABLE_NAME);
    }

    public Optional<Restriction> getByName(String name){
        try {
            return Optional.of(SessionService.getCurrentSession()
                    .createQuery(String.format("SELECT r FROM %s r WHERE r.name = :name", TABLE_NAME),
                            Restriction.class)
                    .setParameter("name", name)
                    .uniqueResult());
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean deleteByName(String name) {
        try {
            Optional<Restriction> opt = getByName(name);
            if(opt.isEmpty()) return false;
            Restriction r = opt.get();
            r.getApplication().setRestriction(null);
            return delete(r);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return false;
    }
}
