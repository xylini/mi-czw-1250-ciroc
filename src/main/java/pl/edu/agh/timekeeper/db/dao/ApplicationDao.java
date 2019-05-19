package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.Optional;

public class ApplicationDao extends DaoBase<Application> {

    private static final String TABLE_NAME = Application.class.getName();

    public ApplicationDao() {
        super(Application.class, TABLE_NAME);
    }

    public Optional<Application> getByName(String name) {
        try {
            return Optional.of(SessionService.getCurrentSession()
                    .createQuery(String.format("SELECT t FROM %s t WHERE t.name = :name", TABLE_NAME), Application.class)
                    .setParameter("name", name).getSingleResult());
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Application> getByPath(String path) {
        try {
            return Optional.of(SessionService.getCurrentSession()
                    .createQuery(String.format("SELECT t FROM %s t WHERE t.path = :path", TABLE_NAME), Application.class)
                    .setParameter("path", path).getSingleResult());
        } catch (PersistenceException e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean addRestriction(Application a, Restriction r) {
        if (a.getRestriction() != null) return false;
        a.setRestriction(r);
        r.setApplication(a);
        return update(a);
    }

    public boolean updateRestriction(Application a, Restriction r) {
        if (a.getRestriction() == null) return false;
        Restriction oldRestriction = a.getRestriction();
        a.setRestriction(r);
        r.setApplication(a);
        return update(a);
    }

    public boolean deleteRestriction(Application a) {
        Restriction oldRestriction = a.getRestriction();
        if (oldRestriction == null) return false;
        a.setRestriction(null);
        return update(a);
    }

    public boolean addToGroup(Application a, Group g) {
        if (g.getApplications().contains(a)) return false;
        if (a.getGroup() != null) return false;
        g.addApplication(a);
        a.setGroup(g);
        return update(a);
    }

    public boolean removeFromGroup(Application a, Group g) {
        if (!g.getApplications().contains(a)) return false;
        if (a.getGroup() == null) return false;
        g.removeApplication(a);
        a.setGroup(null);
        return update(a);
    }
}
