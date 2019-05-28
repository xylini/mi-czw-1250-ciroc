package pl.edu.agh.timekeeper.db.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;

public abstract class LogDaoBase<T, E> extends DaoBase<T> {

    public LogDaoBase(Class<T> classParameter, String tableName) {
        super(classParameter, tableName);
    }

    public abstract Optional<LinkedHashMap<Date, Long>> getHourlyUsageInSecs(E entity, Date date);

    public abstract Optional<LinkedHashMap<Date, Long>> getDailyUsageInSecs(E entity, Date date);

    public abstract Optional<LinkedHashMap<E, Long>> getTotalUsageForAllEntities();
}
