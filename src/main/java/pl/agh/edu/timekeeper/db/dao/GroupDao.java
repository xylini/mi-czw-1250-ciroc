package pl.agh.edu.timekeeper.db.dao;

import pl.agh.edu.applications.Group;
import pl.agh.edu.restrictions.Restriction;
import pl.agh.edu.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.Optional;

public class GroupDao extends DaoBase<Group> {

    private static final String TABLE_NAME = Group.class.getName();

    public GroupDao() {
        super(Group.class, TABLE_NAME);
    }

    public Optional<Group> getByName(String name) {
        try {
            return Optional.of(SessionService.getCurrentSession()
                    .createQuery(String.format("SELECT t FROM %s t WHERE t.name = :name", TABLE_NAME), Group.class)
                    .setParameter("name", name).getSingleResult());
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean addRestriction(Group g, Restriction r) {
        if (g.getRestriction() != null) return false;
        g.setRestriction(r);
        r.setGroup(g);
        return update(g);
    }

    public boolean updateRestriction(Group g, Restriction r) {
        Restriction oldRestriction = g.getRestriction();
        if (oldRestriction == null) return false;
        g.setRestriction(r);
        r.setGroup(g);
        oldRestriction.setApplication(null);
        return update(g);
    }

    public boolean deleteRestriction(Group g) {
        Restriction oldRestriction = g.getRestriction();
        if (oldRestriction == null) return false;
        g.setRestriction(null);
        oldRestriction.setApplication(null);
        return update(g);
    }
}
