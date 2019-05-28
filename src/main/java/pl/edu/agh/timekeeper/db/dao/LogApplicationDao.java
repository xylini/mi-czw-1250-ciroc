package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.db.SessionService;

import javax.persistence.PersistenceException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public Optional<LinkedHashMap<Date, Long>> getHourlyUsageInSecs(Application app, Date day) {
        return getUsageInSecs(
                app,
                date -> isDateBetween(date, day, nextDay(day)),
                this::getHourFrom,
                (logStart, logEnd) -> isLogOfDay(logStart, logEnd, day),
                this::nextHour);
    }

    public Optional<LinkedHashMap<Date, Long>> getDailyUsageInSecs(Application app, Date month) {
        Date nextMonth = Date.from(ZonedDateTime
                .ofInstant(month.toInstant(), ZoneId.systemDefault())
                .plusMonths(1)
                .toInstant());
        return getUsageInSecs(
                app,
                date -> isDateBetween(date, month, nextMonth),
                this::getDayFrom,
                (logStart, logEnd) -> isLogOfMonth(logStart, logEnd, month),
                this::nextDay);
    }

    public Optional<LinkedHashMap<Application, Long>> getTotalUsageForAllEntities() {
        List<LogApplication> l = getAll();
        if (l.isEmpty()) return Optional.empty();
        LinkedHashMap<Application, Long> stats = new LinkedHashMap<>();
        List<LogApplication> appLogs = l.stream()
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
            Application app,
            Function<Date, Boolean> hasProperStartTime,
            Function<Date, Date> getLogDateInStatUnit,
            BiFunction<Date, Date, Boolean> isLogOfSpecifiedPeriod,
            Function<Date, Date> getNextStatDate) {
        Optional<List<LogApplication>> logs = getAll(app);
        if (logs.isEmpty()) return Optional.empty();

        List<LogApplication> l = logs.get()
                .stream()
                .filter(log -> isLogOfSpecifiedPeriod.apply(log.getTimeStart(), log.getTimeEnd()))
                .collect(Collectors.toList());
        LinkedHashMap<Date, Long> stats = new LinkedHashMap<>();

        for (LogApplication log : l) {
            Date logStartInStatUnit = getLogDateInStatUnit.apply(log.getTimeStart());
            Date logEndInStatUnit = getLogDateInStatUnit.apply(log.getTimeEnd());
            insertStat(stats, log.getTimeStart(), log.getTimeEnd(), logStartInStatUnit, logEndInStatUnit,
                    hasProperStartTime, getNextStatDate);
        }
        return Optional.of(stats);
    }

    private void insertStat(
            LinkedHashMap<Date, Long> stats,
            Date logStart,
            Date logEnd,
            Date logStartInStatUnit,
            Date logEndInStatUnit,
            Function<Date, Boolean> hasProperStartTime,
            Function<Date, Date> getNextStatDate) {
        Date nextStatDate = getNextStatDate.apply(logStartInStatUnit);
        if (logStartInStatUnit.equals(logEndInStatUnit) || logEnd.equals(nextStatDate)) {
            Long usage = (logEnd.getTime() - logStart.getTime()) / 1000;
            if (!stats.keySet().contains(logStartInStatUnit)) {
                stats.put(logStartInStatUnit, usage);
            } else {
                stats.replace(logStartInStatUnit, stats.get(logStartInStatUnit) + usage);
            }
        } else {
            if (hasProperStartTime.apply(logStartInStatUnit)) {
                insertStat(stats, logStart, nextStatDate, logStartInStatUnit, nextStatDate,
                        hasProperStartTime, getNextStatDate);
            }
            if (hasProperStartTime.apply(nextStatDate)) {
                insertStat(stats, nextStatDate, logEnd, nextStatDate, logEndInStatUnit,
                        hasProperStartTime, getNextStatDate);
            }
        }
    }

    private Date getHourFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    private Date getDayFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(getHourFrom(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    private Date getMonthFrom(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(getDayFrom(date));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Boolean isLogOfDay(Date logStart, Date logEnd, Date day) {
        return day.equals(getDayFrom(logStart))
                || day.equals(getDayFrom(logEnd))
                || (day.after(logStart) && day.before(logEnd));
    }

    private Boolean isLogOfMonth(Date logStart, Date logEnd, Date month) {
        return month.equals(getMonthFrom(logStart))
                || month.equals(getMonthFrom(logEnd))
                || (month.after(logStart) && month.before(logEnd));
    }

    private Boolean isDateBetween(Date date, Date start, Date end) {
        return !(date.before(start) && date.after(end));
    }

    private Date nextHour(Date d) {
        return Date.from(d.toInstant().plusSeconds(3600));
    }

    private Date nextDay(Date d) {
        return Date.from(d.toInstant().plusSeconds(3600 * 24));
    }
}