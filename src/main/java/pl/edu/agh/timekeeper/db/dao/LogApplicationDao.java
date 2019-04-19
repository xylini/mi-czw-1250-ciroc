package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogApplicationDao extends DaoBase<LogApplication> {

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
                    .setParameter("app", a).getResultList());
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<LinkedHashMap<Date, Long>> getHourlyUsageInSecs(Application a) {
        return getUsageInSecs(a, this::getHourDateFrom);
    }

    public Optional<LinkedHashMap<Date, Long>> getDailyUsageInSecs(Application a) {
        return getUsageInSecs(a, this::getDayDateFrom);
    }

    public Optional<LinkedHashMap<Date, Long>> getMonthlyUsageInSecs(Application a) {
        return getUsageInSecs(a, this::getMonthDateFrom);
    }

    public Optional<LinkedHashMap<Application, Long>> getTotalUsageForAllApps() {
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

    private Optional<LinkedHashMap<Date, Long>> getUsageInSecs(Application a, Function<Date, Date> converter) {
        Optional<List<LogApplication>> l = getAll(a);
        if (l.isEmpty()) return Optional.empty();
        LinkedHashMap<Date, Long> stats = new LinkedHashMap<>();

        for (LogApplication log : l.get()) {
            Date dayDate = converter.apply(log.getTimeStart());
            Long usage = (log.getTimeEnd().getTime() - log.getTimeStart().getTime()) / 1000;
            if (!stats.keySet().contains(dayDate)) {
                stats.put(dayDate, usage);
            } else {
                stats.replace(dayDate, stats.get(dayDate) + usage);
            }
        }
        return Optional.of(stats);
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

}