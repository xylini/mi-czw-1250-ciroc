package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogApplicationDao extends LogDaoBase<LogApplication, Application> {

    private static final String TABLE_NAME = LogApplication.class.getName();

    public LogApplicationDao() {
        super(LogApplication.class, TABLE_NAME);
    }

    public Optional<List<LogApplication>> getAll(Application a) {
        try {
            return Optional.of(SessionService.getCurrentSession()
                    .createQuery(
                            "SELECT l " +
                                    "FROM " + TABLE_NAME + " l " +
                                    "WHERE l.application = :app", LogApplication.class)
                    .setParameter("app", a)
                    .getResultList());
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<LinkedHashMap<Date, Long>> getHourlyUsageInSecs(Application a, Date dayDate) {
        return getUsageInSecs(a, this::getHourDateFrom, d -> sameDayPredicate(d, dayDate));
    }

    public Optional<LinkedHashMap<Date, Long>> getDailyUsageInSecs(Application a, Date monthDate) {
        return getUsageInSecs(a, this::getDayDateFrom, d -> sameMonthPredicate(d, monthDate));
    }

    public Optional<LinkedHashMap<Date, Long>> getMonthlyUsageInSecs(Application a) {
        return getUsageInSecs(a, this::getMonthDateFrom, d -> true);
    }

    public Optional<LinkedHashMap<Application, Long>> getTotalUsageForAllEntities() {
        Optional<List<LogApplication>> l = getAll();
        if (l.isEmpty()) return Optional.empty();
        LinkedHashMap<Application, Long> stats = new LinkedHashMap<>();
        List<LogApplication> appLogs = l.get()
                .stream()
                .filter(logApp -> logApp.getApplication() != null)
                .collect(Collectors.toList());

        for (LogApplication log : appLogs) {
            Application app = log.getApplication();
            Long usage = (log.getTimeEnd().getTime() - log.getTimeStart().getTime()) / 1000;
            if (stats.keySet().contains(app)) {
                stats.replace(app, stats.get(app) + usage);
            } else {
                stats.put(app, usage);
            }
        }
        return Optional.of(stats);
    }

    private Optional<LinkedHashMap<Date, Long>> getUsageInSecs(
            Application a,
            Function<Date, Date> converter,
            Function<Date, Boolean> filter) {
        Optional<List<LogApplication>> logs = getAll(a);
        if (logs.isEmpty()) return Optional.empty();
        List<LogApplication> l = logs.get().stream().filter(log -> filter.apply(log.getTimeStart())).collect(Collectors.toList());
        LinkedHashMap<Date, Long> stats = new LinkedHashMap<>();

        for (LogApplication log : l) {
            Date startDate = converter.apply(log.getTimeStart());
            Date endDate = converter.apply(log.getTimeEnd());
            if(startDate.equals(endDate)) {
                Long usage = (log.getTimeEnd().getTime() - log.getTimeStart().getTime()) / 1000;
                insertStat(stats, startDate, usage);
            } else {
                Long usage1 = (endDate.getTime() - log.getTimeStart().getTime()) / 1000;
                Long usage2 = (log.getTimeEnd().getTime() - endDate.getTime()) / 1000;
                insertStat(stats, startDate, usage1);
                insertStat(stats, endDate, usage2);
            }
        }
        return Optional.of(stats);
    }

    private void insertStat(LinkedHashMap<Date, Long> stats, Date d, Long val){
        if (!stats.keySet().contains(d)) {
            stats.put(d, val);
        } else {
            stats.replace(d, stats.get(d) + val);
        }
    }

    private Date getHourDateFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    private Date getDayDateFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(getHourDateFrom(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    private Date getMonthDateFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(getDayDateFrom(date));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Boolean sameDayPredicate(Date d, Date dayDate){
        return dayDate.equals(getDayDateFrom(d));
    }

    private Boolean sameMonthPredicate(Date d, Date monthDate){
        return monthDate.equals(getMonthDateFrom(d));
    }
}