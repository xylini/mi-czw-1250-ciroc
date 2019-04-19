package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Restriction;

public class RestrictionDao extends DaoBase<Restriction> {

    private static final String TABLE_NAME = Restriction.class.getName();

    public RestrictionDao() {
        super(Restriction.class, TABLE_NAME);
    }
}
