package pl.agh.edu.timekeeper.db.dao;

import pl.agh.edu.logs.LogGroup;

public class LogGroupDao extends DaoBase<LogGroup> {

    private static final String TABLE_NAME = LogGroup.class.getName();

    public LogGroupDao() {
        super(LogGroup.class, TABLE_NAME);
    }
}