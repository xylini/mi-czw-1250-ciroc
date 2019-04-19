package pl.agh.edu.timekeeper.db.dao;

import pl.agh.edu.restrictions.Restriction;

public class RestrictionDao extends DaoBase<Restriction> {

    private static final String TABLE_NAME = Restriction.class.getName();

    public RestrictionDao() {
        super(Restriction.class, TABLE_NAME);
    }
}
