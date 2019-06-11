package pl.edu.agh.timekeeper.db.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.log.LogApplication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;

public class LogApplicationDaoTest extends DaoTestBase<LogApplicationDao, LogApplication> {

    private static final ApplicationDao applicationDao = new ApplicationDao();

    private static final LogApplicationDao logAppDao = new LogApplicationDao();

    public LogApplicationDaoTest() {
        super(logAppDao);
    }

    @Override
    protected LogApplication getEntity() {
        return new LogApplication();
    }


    @Test
    void getDailyUsageInSecsTest() {
        // given
        String path = "/byleco"; //Krystian: pozwolilem sobie chwilowo dodac taka opcje, najwyzej sobie to pozniej zmienisz :)
        Application a1 = new Application("app1", path);
        Application a2 = new Application("app2", path + "2");
        LogApplication log1 = new LogApplication(a1);
        LogApplication log2 = new LogApplication(a1);
        LogApplication log3 = new LogApplication(a1);
        LogApplication log4 = new LogApplication(a2);

        applicationDao.create(a1);
        applicationDao.create(a2);

        logAppDao.create(log1);
        logAppDao.create(log2);
        logAppDao.create(log3);
        logAppDao.create(log4);

        ZonedDateTime d1 = LocalDateTime.of(2019, 4, 19, 13, 21).atZone(ZoneId.systemDefault());
        Instant i1 = d1.toInstant();

        log1.setTimeStart(Date.from(d1.toInstant()));
        log1.setTimeEnd(Date.from(d1.plusMinutes(10).toInstant())); // 10 min
        logAppDao.update(log1);

        log2.setTimeStart(Date.from(d1.plusMinutes(20).toInstant()));
        log2.setTimeEnd(Date.from(d1.plusMinutes(40).toInstant())); // 20 min
        logAppDao.update(log2);

        log3.setTimeStart(Date.from(d1.plusDays(1).plusMinutes(50).toInstant()));
        log3.setTimeEnd(Date.from(d1.plusDays(1).plusHours(1).toInstant())); // 10 min different day
        logAppDao.update(log3);

        log4.setTimeStart(Date.from(d1.plusMinutes(10).toInstant()));
        log4.setTimeEnd(Date.from(d1.plusMinutes(15).toInstant())); // 5 min different app
        logAppDao.update(log4);

        ZonedDateTime keyZDT = LocalDateTime.of(2019, 4, 19, 0, 0)
                .atZone(ZoneId.systemDefault());
        Date keyDate1 = Date.from(keyZDT.toInstant());
        Date keyDate2 = Date.from(keyZDT.plusDays(1).toInstant());

        Date monthDate = Date.from(keyZDT.withDayOfMonth(1).toInstant());

        // when
        LinkedHashMap<Date, Long> stats1 = logAppDao.getDailyUsageInMillis(a1, monthDate);
        LinkedHashMap<Date, Long> stats2 = logAppDao.getDailyUsageInMillis(a2, monthDate);
        // then
        // a1
        Assertions.assertFalse(stats1.isEmpty());
        Assertions.assertEquals(2, stats1.keySet().size());
        for (Date key : stats1.keySet()) System.out.println(key);
        System.out.println("key: " + keyDate1);
        Assertions.assertTrue(stats1.containsKey(keyDate1));
        Assertions.assertTrue(stats1.containsKey(keyDate2));
        Assertions.assertEquals(Long.valueOf(1800000), stats1.get(keyDate1));
        Assertions.assertEquals(Long.valueOf(600000), stats1.get(keyDate2));
        // a2
        Assertions.assertFalse(stats2.isEmpty());
        Assertions.assertEquals(1, stats2.keySet().size());
        Assertions.assertTrue(stats2.containsKey(keyDate1));
        Assertions.assertEquals(Long.valueOf(300000), stats2.get(keyDate1));
    }
}
