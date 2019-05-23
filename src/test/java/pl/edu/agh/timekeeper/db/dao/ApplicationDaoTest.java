package pl.edu.agh.timekeeper.db.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.model.*;
import pl.edu.agh.timekeeper.log.LogApplication;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ApplicationDaoTest extends DaoTestBase<ApplicationDao, Application> {

    private static final ApplicationDao applicationDao = new ApplicationDao();

    private static final RestrictionDao restrictionDao = new RestrictionDao();

    private static final LogApplicationDao logAppDao = new LogApplicationDao();

    private static final GroupDao groupDao = new GroupDao();

    public ApplicationDaoTest() {
        super(applicationDao);
    }

    @Override
    protected Application getEntity() {
        return new Application("some application", "/byleco"); //Krystian: pozwolilem sobie chwilowo dodac taka opcje, najwyzej sobie to pozniej zmienisz :)
    }

    @Test
    void getByNameTest() {
        // given
        Application entity = getEntity();
        applicationDao.create(entity);
        // when
        Optional<Application> selectedEntity = applicationDao.getByName(entity.getName());
        // then
        Assertions.assertTrue(selectedEntity.isPresent());
        Assertions.assertEquals(entity, selectedEntity.get());
    }

    @Test
    void createTest() {
        // given
        String path = "/byleco"; //Krystian: pozwolilem sobie chwilowo dodac taka opcje, najwyzej sobie to pozniej zmienisz :)
        Application app1 = new Application("App1", path);
        Application app2 = new Application("App2", path + "2");
        Application app3 = new Application("App1", path);
        // when
        Optional<Application> first = applicationDao.create(app1);
        Optional<Application> second = applicationDao.create(app2);
        Optional<Application> third = applicationDao.create(app3);
        // then
        Assertions.assertTrue(first.isPresent());
        Assertions.assertTrue(second.isPresent());
        Assertions.assertNotEquals(first.get().getId(), second.get().getId());
        // TODO: (unique name constraint) Assertions.assertFalse(third.isPresent());
    }

    @Test
    void addRestrictionTest() {
        // given
        Application application = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .setLimit(new MyTime(1, 0))
                .build();
        applicationDao.create(application);
        restrictionDao.create(restriction);
        // when
        boolean result = applicationDao.addRestriction(application, restriction);
        // then
        Assertions.assertTrue(result);
        Assertions.assertEquals(restriction, applicationDao.getById(application.getId()).get().getRestriction());
        Assertions.assertEquals(application, restrictionDao.getById(restriction.getId()).get().getApplication());
        Assertions.assertFalse(applicationDao.addRestriction(application, restriction));
    }

    @Test
    void updateRestrictionWhenNoRestrictionSetTest() {
        // given
        Application application = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        applicationDao.create(application);
        restrictionDao.create(restriction);
        // when
        boolean result1 = applicationDao.updateRestriction(application, restriction);
        // then
        Assertions.assertFalse(result1);
    }

    @Test
    void updateRestrictionWhenRestrictionSetTest() {
        // given
        Application application = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        Restriction restriction2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(3, 0)))
                .build();
        applicationDao.create(application);
        restrictionDao.create(restriction);
        restrictionDao.create(restriction2);
        applicationDao.addRestriction(application, restriction);
        // when
        boolean result2 = applicationDao.updateRestriction(application, restriction2);
        // then
        Assertions.assertTrue(result2);
        Assertions.assertEquals(restriction2, applicationDao.getById(application.getId()).get().getRestriction());
        Assertions.assertEquals(application, restrictionDao.getById(restriction2.getId()).get().getApplication());
        Assertions.assertEquals(restriction2, application.getRestriction());
        Assertions.assertEquals(application, restriction2.getApplication());
        Assertions.assertFalse(applicationDao.addRestriction(application, restriction));
        Assertions.assertEquals(1, restrictionDao.getAll().get().size());
    }

    @Test
    void removeExistingRestrictionTest() {
        // given
        Application application = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        Application application1 = applicationDao.create(application).get();
        System.out.println("ID: " + application1.getId());
        restrictionDao.create(restriction);
        applicationDao.addRestriction(application1, restriction);
        // when
        boolean result = applicationDao.deleteRestriction(application1);
        // then
        Assertions.assertTrue(result);
        Assertions.assertNull(applicationDao.getById(application1.getId()).get().getRestriction());
        Assertions.assertTrue(restrictionDao.getById(restriction.getId()).isEmpty());
        Assertions.assertTrue(restrictionDao.getAll().get().isEmpty());
    }

    @Test
    void removeNotExistingRestrictionTest() {
        // given
        Application application = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        applicationDao.create(application);
        restrictionDao.create(restriction);
        // when
        boolean result = applicationDao.deleteRestriction(application);
        // then
        Assertions.assertFalse(result);
    }

    @Test
    void addLogTest() {
        // given
        Application application = getEntity();
        applicationDao.create(application);
        LogApplication log = new LogApplication(application);
        log.setTimeStart(new Date());
        log.setTimeEnd(new Date());
        logAppDao.create(log);
        // when
        application.addLogApplication(log);
        // then
        Assertions.assertTrue(application.getLogApplications().contains(log));
        Optional<List<LogApplication>> logApps = logAppDao.getAll();
        Assertions.assertTrue(logApps.isPresent());
        Assertions.assertTrue(logApps.get().contains(log));
    }

    @Test
    void addToGroupTest() {
        // given
        Application application = getEntity();
        applicationDao.create(application);
        Group group = new Group("group");
        groupDao.create(group);
        // when
        boolean result = applicationDao.addToGroup(application, group);
        // then
        Assertions.assertTrue(result);
        Application app = applicationDao.getById(application.getId()).get();
        Group g = groupDao.getById(group.getId()).get();
        Assertions.assertEquals(group, app.getGroup());
        Assertions.assertTrue(g.getApplications().contains(application));
    }

    @Test
    void removeFromGroupTest() {
        // given
        Application application = getEntity();
        applicationDao.create(application);
        Group group = new Group("group");
        groupDao.create(group);
        applicationDao.addToGroup(application, group);
        // when
        boolean result = applicationDao.removeFromGroup(application, group);
        // then
        Assertions.assertTrue(result);
        Application app = applicationDao.getById(application.getId()).get();
        Group g = groupDao.getById(group.getId()).get();
        Assertions.assertNull(app.getGroup());
        Assertions.assertFalse(g.getApplications().contains(application));
    }
}
