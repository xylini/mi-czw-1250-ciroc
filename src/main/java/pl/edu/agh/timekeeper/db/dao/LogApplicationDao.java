package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.function.BiFunction;
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
        return getUsageInSecs(a, this::getHourDateFrom, (start, end) -> dayDatePredicate(start, end, dayDate), this::nextHourStep);
    }

    public Optional<LinkedHashMap<Date, Long>> getDailyUsageInSecs(Application a, Date monthDate) {
        return getUsageInSecs(a, this::getDayDateFrom, (start, end) -> monthDatePredicate(start, end, monthDate), this::nextDayStep);
    }

    /*public Optional<LinkedHashMap<Date, Long>> getMonthlyUsageInSecs(Application a) {
        return getUsageInSecs(a, this::getMonthDateFrom, d -> true, d -> d);
    }*/

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
            BiFunction<Date, Date, Boolean> filter,
            Function<Date, Date> step) {
        Optional<List<LogApplication>> logs = getAll(a);
        if (logs.isEmpty()) return Optional.empty();
        List<LogApplication> l = logs.get().stream().filter(log -> filter.apply(log.getTimeStart(), log.getTimeEnd())).collect(Collectors.toList());
        LinkedHashMap<Date, Long> stats = new LinkedHashMap<>();

        for (LogApplication log : l) {
            Date startDate = converter.apply(log.getTimeStart());
            Date endDate = converter.apply(log.getTimeEnd());
            insertStat(stats, log.getTimeStart(), log.getTimeEnd(), startDate, endDate, step);
        }
        return Optional.of(stats);
    }

    private void insertStat(
            LinkedHashMap<Date, Long> stats,
            Date logTimeStart,
            Date logTimeEnd,
            Date startDate,
            Date endDate,
            Function<Date, Date> step){
        Date nextTickDate = step.apply(startDate);
        if(startDate.equals(endDate) || logTimeEnd.equals(nextTickDate)) {
            Long usage = (logTimeEnd.getTime() - logTimeStart.getTime()) / 1000;
            if (!stats.keySet().contains(startDate)) {
                stats.put(startDate, usage);
            } else {
                stats.replace(startDate, stats.get(startDate) + usage);
            }
        } else {
            insertStat(stats, logTimeStart, nextTickDate, startDate, nextTickDate, step);
            insertStat(stats, nextTickDate, logTimeEnd, nextTickDate, endDate, step);
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

    private Boolean dayDatePredicate(Date start, Date end, Date statDate){
        return statDate.equals(getDayDateFrom(start))
                || statDate.equals(getDayDateFrom(end))
                || (statDate.after(start) && statDate.before(end));
    }

    private Boolean monthDatePredicate(Date start, Date end, Date statDate){
        return statDate.equals(getMonthDateFrom(start))
                || statDate.equals(getMonthDateFrom(end))
                || (statDate.after(start) && statDate.before(end));
    }

    private Date nextHourStep(Date d){
        return Date.from(d.toInstant().plusSeconds(3600));
    }

    private Date nextDayStep(Date d){
        return Date.from(d.toInstant().plusSeconds(3600 * 24));
    }

}