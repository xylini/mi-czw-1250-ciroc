package pl.edu.agh.timekeeper.db.dao;

import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogDao extends DaoBase<LogApplication> {

    private static final String TABLE_NAME = LogApplication.class.getName();

    public LogDao() {
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

    public long getUsageInMillisOn(Application app, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay();
        Long res = SessionService.getCurrentSession()
                .createQuery(
                        "SELECT SUM(DATEDIFF(MILLISECOND, GREATEST(l.timeStart, :start_t), LEAST(l.timeEnd, :end_t))) " +
                                "FROM " + TABLE_NAME + " l " +
                                "WHERE l.application = :app " +
                                "AND (l.timeStart BETWEEN :start_t AND :end_t " +
                                "OR l.timeEnd BETWEEN :start_t AND :end_t " +
                                "OR (l.timeStart < :start_t AND l.timeEnd > :end_t))",
                        Long.class)
                .setParameter("app", app)
                .setParameter("start_t", Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .setParameter("end_t", Date.from(end.atZone(ZoneId.systemDefault()).toInstant()))
                .getSingleResult();
        if (res == null) return 0L;
        return res;
    }

    public long getUsageInMillisOn(Group group, LocalDate day){
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay();
        Long res = SessionService.getCurrentSession()
                .createQuery(
                        "SELECT SUM(DATEDIFF(MILLISECOND, GREATEST(l.timeStart, :start_t), LEAST(l.timeEnd, :end_t))) " +
                                "FROM LogApplication l " +
                                "WHERE l.application.group = :grp " +
                                "AND (l.timeStart BETWEEN :start_t AND :end_t " +
                                "OR l.timeEnd BETWEEN :start_t AND :end_t " +
                                "OR (l.timeStart < :start_t AND l.timeEnd > :end_t))",
                        Long.class)
                .setParameter("grp", group)
                .setParameter("start_t", Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .setParameter("end_t", Date.from(end.atZone(ZoneId.systemDefault()).toInstant()))
                .getSingleResult();
        if(res == null) return 0L;
        return res;
    }

    public LinkedHashMap<Date, Long> getHourlyUsageInMillis(Group group, Date day){
        LocalDate date = LocalDate.ofInstant(day.toInstant(), ZoneId.systemDefault());
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        List<UsageStat> res = SessionService.getCurrentSession()
                .createQuery(
                        "SELECT new pl.edu.agh.timekeeper.db.dao.UsageStat(" +
                                "    YEAR(l.timeStart), " +
                                "    MONTH(l.timeStart), " +
                                "    DAY(l.timeStart), " +
                                "    HOUR(l.timeStart), " +
                                "    SUM(DATEDIFF(MILLISECOND, GREATEST(l.timeStart, :start_t), LEAST(l.timeEnd, :end_t)))) " +
                                "FROM LogApplication l " +
                                "WHERE l.application.group = :grp " +
                                "AND (l.timeStart BETWEEN :start_t AND :end_t " +
                                "OR l.timeEnd BETWEEN :start_t AND :end_t " +
                                "OR (l.timeStart < :start_t AND l.timeEnd > :end_t)) " +
                                "GROUP BY YEAR(l.timeStart), " +
                                "    MONTH(l.timeStart), " +
                                "    DAY(l.timeStart), " +
                                "    HOUR(l.timeStart) ",
                        UsageStat.class)
                .setParameter("grp", group)
                .setParameter("start_t", startDate)
                .setParameter("end_t", Date.from(end.atZone(ZoneId.systemDefault()).toInstant()))
                .getResultList();
        LinkedHashMap<Date, Long> result = createResultMap(startDate, res);
        getDateStream(start, end, ChronoUnit.HOURS).forEach(d -> {
            if(!result.containsKey(d)) result.put(d, 0L);
        });
        return result;
    }

    public LinkedHashMap<Date, Long> getDailyUsageInMillis(Group group, Date day){
        LocalDate date = LocalDate.ofInstant(day.toInstant(), ZoneId.systemDefault());
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusMonths(1).atStartOfDay();
        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        List<UsageStat> res = SessionService.getCurrentSession()
                .createQuery(
                        "SELECT new pl.edu.agh.timekeeper.db.dao.UsageStat(" +
                                "    YEAR(l.timeStart), " +
                                "    MONTH(l.timeStart), " +
                                "    DAY(l.timeStart), " +
                                "    SUM(DATEDIFF(MILLISECOND, GREATEST(l.timeStart, :start_t), LEAST(l.timeEnd, :end_t)))) " +
                                "FROM LogApplication l " +
                                "WHERE l.application.group = :grp " +
                                "AND (l.timeStart BETWEEN :start_t AND :end_t " +
                                "OR l.timeEnd BETWEEN :start_t AND :end_t " +
                                "OR (l.timeStart < :start_t AND l.timeEnd > :end_t)) " +
                                "GROUP BY YEAR(l.timeStart), " +
                                "    MONTH(l.timeStart), " +
                                "    DAY(l.timeStart) ",
                        UsageStat.class)
                .setParameter("grp", group)
                .setParameter("start_t", startDate)
                .setParameter("end_t", Date.from(end.atZone(ZoneId.systemDefault()).toInstant()))
                .getResultList();
        createResultMap(startDate, res);
        LinkedHashMap<Date, Long> result = createResultMap(startDate, res);
        getDateStream(start, end, ChronoUnit.DAYS).forEach(d -> {
            if(!result.containsKey(d)) result.put(d, 0L);
        });
        return result;
    }

    private LinkedHashMap<Date, Long> createResultMap(Date startDate, List<UsageStat> res) {
        LinkedHashMap<Date, Long> result = new LinkedHashMap<>();
        res.forEach(s -> {
            if(s.getDate().before(startDate)){
                result.put(startDate, s.getUsage());
            } else {
                result.put(s.getDate(), s.getUsage());
            }
        });
        return result;
    }

    public LinkedHashMap<Date, Long> getHourlyUsageInMillis(Application app, Date day) {
        return getUsageInMillis(
                app,
                date -> isDateBetween(date, day, nextDay(day)),
                this::getHourFrom,
                (logStart, logEnd) -> isLogOfDay(logStart, logEnd, day),
                this::nextHour);
    }

    public LinkedHashMap<Date, Long> getDailyUsageInMillis(Application app, Date month) {
        Date nextMonth = Date.from(ZonedDateTime
                .ofInstant(month.toInstant(), ZoneId.systemDefault())
                .plusMonths(1)
                .toInstant());
        return getUsageInMillis(
                app,
                date -> isDateBetween(date, month, nextMonth),
                this::getDayFrom,
                (logStart, logEnd) -> isLogOfMonth(logStart, logEnd, month),
                this::nextDay);
    }

    public LinkedHashMap<Application, Long> getTotalUsageForAllEntities() {
        List<LogApplication> l = getAll();
        LinkedHashMap<Application, Long> stats = new LinkedHashMap<>();
        if (l.isEmpty()) return stats;
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
        return stats;
    }

    private LinkedHashMap<Date, Long> getUsageInMillis(
            Application app,
            Function<Date, Boolean> hasProperStartTime,
            Function<Date, Date> getLogDateInStatUnit,
            BiFunction<Date, Date, Boolean> isLogOfSpecifiedPeriod,
            Function<Date, Date> getNextStatDate) {
        Optional<List<LogApplication>> logs = getAll(app);
        LinkedHashMap<Date, Long> stats = new LinkedHashMap<>();
        if (logs.isEmpty()) return stats;

        List<LogApplication> l = logs.get()
                .stream()
                .filter(log -> isLogOfSpecifiedPeriod.apply(log.getTimeStart(), log.getTimeEnd()))
                .collect(Collectors.toList());

        for (LogApplication log : l) {
            Date logStartInStatUnit = getLogDateInStatUnit.apply(log.getTimeStart());
            Date logEndInStatUnit = getLogDateInStatUnit.apply(log.getTimeEnd());
            insertStat(stats, log.getTimeStart(), log.getTimeEnd(), logStartInStatUnit, logEndInStatUnit,
                    hasProperStartTime, getNextStatDate);
        }
        return stats;
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
            Long usage = (logEnd.getTime() - logStart.getTime());
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

    private Stream<Date> getDateStream(LocalDateTime start, LocalDateTime end, ChronoUnit timeUnit){
        return Stream.iterate(start, date -> date.plus(1, timeUnit))
                .limit(start.until(end, timeUnit))
                .map(dt -> Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
    }
}