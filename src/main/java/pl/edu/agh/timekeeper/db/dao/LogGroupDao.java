package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.log.LogGroup;

public class LogGroupDao extends DaoBase<LogGroup> {

    private static final String TABLE_NAME = LogGroup.class.getName();

    public LogGroupDao() {
        super(LogGroup.class, TABLE_NAME);
    }
}