package pl.edu.agh.timekeeper.db.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.log.LogGroup;
import pl.edu.agh.timekeeper.model.MyTime;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.model.TimePair;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class GroupDaoTest extends DaoTestBase<GroupDao, Group> {

    private static final GroupDao groupDao = new GroupDao();

    private static final RestrictionDao restrictionDao = new RestrictionDao();

    private static final LogGroupDao logGroupDao = new LogGroupDao();

    public GroupDaoTest() {
        super(groupDao);
    }

    @Override
    protected Group getEntity() {
        return new Group("some group");
    }

    @Test
    void getByNameTest() {
        // given
        Group entity = getEntity();
        groupDao.create(entity);
        // when
        Optional<Group> selectedEntity = groupDao.getByName(entity.getName());
        // then
        Assertions.assertTrue(selectedEntity.isPresent());
        Assertions.assertEquals(entity, selectedEntity.get());
    }

    @Test
    void createTest() {
        // given
        Group g1 = new Group("G1");
        Group g2 = new Group("G2");
        Group g11 = new Group("G1");
        // when
        Optional<Group> first = groupDao.create(g1);
        Optional<Group> second = groupDao.create(g2);
        Optional<Group> third = groupDao.create(g11);
        // then
        Assertions.assertTrue(first.isPresent());
        Assertions.assertTrue(second.isPresent());
        Assertions.assertNotEquals(first.get().getId(), second.get().getId());
        //Assertions.assertFalse(third.isPresent());
    }

    @Test
    void addRestrictionTest() {
        // given
        Group group = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        groupDao.create(group);
        restrictionDao.create(restriction);
        // when
        boolean result = groupDao.addRestriction(group, restriction);
        // then
        Assertions.assertTrue(result);
        Assertions.assertEquals(restriction, group.getRestriction());
        Assertions.assertEquals(group, restriction.getGroup());
        Assertions.assertFalse(groupDao.addRestriction(group, restriction));
    }

    @Test
    void updateRestrictionWhenNoRestrictionSetTest() {
        // given
        Group group = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        groupDao.create(group);
        restrictionDao.create(restriction);
        // when
        boolean result1 = groupDao.updateRestriction(group, restriction);
        // then
        Assertions.assertFalse(result1);
    }

    @Test
    void updateRestrictionWhenRestrictionSetTest() {
        // given
        Group group = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        Restriction restriction2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(3, 0)))
                .build();
        groupDao.create(group);
        restrictionDao.create(restriction);
        restrictionDao.create(restriction2);
        groupDao.addRestriction(group, restriction);
        // when
        boolean result2 = groupDao.updateRestriction(group, restriction2);
        // then
        Assertions.assertTrue(result2);
        Assertions.assertEquals(restriction2, group.getRestriction());
        Assertions.assertEquals(group, restriction2.getGroup());
        Assertions.assertFalse(groupDao.addRestriction(group, restriction));
        Assertions.assertEquals(1, restrictionDao.getAll().get().size());
    }

    @Test
    void removeExistingRestrictionTest() {
        // given
        Group group = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        groupDao.create(group);
        restrictionDao.create(restriction);
        groupDao.addRestriction(group, restriction);
        // when
        boolean result = groupDao.deleteRestriction(group);
        // then
        Assertions.assertTrue(result);
        Assertions.assertNull(group.getRestriction());
        Assertions.assertTrue(restrictionDao.getAll().get().isEmpty());
    }

    @Test
    void removeNotExistingRestrictionWhenNoRestrictionSetTest() {
        // given
        Group group = getEntity();
        Restriction restriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(1, 0))
                .addBlockedHours(new TimePair(new MyTime(1, 0), new MyTime(2, 0)))
                .build();
        groupDao.create(group);
        restrictionDao.create(restriction);
        // when
        boolean result = groupDao.deleteRestriction(group);
        // then
        Assertions.assertFalse(result);
    }

    @Test
    void addLogTest() {
        // given
        Group group = getEntity();
        groupDao.create(group);
        LogGroup log = new LogGroup(group);
        log.setTimeStart(new Date());
        log.setTimeEnd(new Date());
        logGroupDao.create(log);
        // when
        group.addLogGroup(log);
        // then
        Assertions.assertTrue(group.getLogGroups().contains(log));
        Optional<List<LogGroup>> logGroups = logGroupDao.getAll();
        Assertions.assertTrue(logGroups.isPresent());
        Assertions.assertTrue(logGroups.get().contains(log));
    }
}