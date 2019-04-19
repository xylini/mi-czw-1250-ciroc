package pl.edu.agh.timekeeper.log;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.model.MyTime;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogGroupTest {
    private Session session;
    private static Configuration configuration;

    @BeforeAll
    static void beforeAll() {
        configuration = new Configuration();
        configuration.configure("hibernate_test.cfg.xml");
        configuration.addAnnotatedClass(Application.class);
        configuration.addAnnotatedClass(Group.class);
        configuration.addAnnotatedClass(LogApplication.class);
        configuration.addAnnotatedClass(LogGroup.class);
        configuration.addAnnotatedClass(Restriction.class);
    }

    @BeforeEach
    void beforeEach() {
        SessionFactory ourSessionFactory = configuration.buildSessionFactory();
        session = ourSessionFactory.openSession();
    }

    @AfterEach
    void afterEach() {
        session.close();
    }

    @Test
    void addTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(new MyTime(2, 2), new MyTime(3, 3), new MyTime(4, 4));
        Group myGroup = new Group("*.mp3", myRestriction);
        LogGroup myLogGroup = new LogGroup(myGroup);
        LogGroup myLogGroup_2 = new LogGroup(myGroup);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myLogGroup);
        session.save(myLogGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogGroup> myLogGroups = session.createQuery(
                "from LogGroup", LogGroup.class).getResultList();

        assertEquals(2, myLogGroups.size());
        assertTrue(myLogGroups.contains(myLogGroup));
        assertTrue(myLogGroups.contains(myLogGroup_2));

        session.getTransaction().commit();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(new MyTime(2, 2), new MyTime(3, 3), new MyTime(4, 4));
        Group myGroup = new Group("*.mp3", myRestriction);
        LogGroup myLogGroup = new LogGroup(myGroup);
        LogGroup myLogGroup_2 = new LogGroup(myGroup);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myLogGroup);
        session.save(myLogGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myGroup.removeLogGroup(myLogGroup);

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogGroup> myLogGroups = session.createQuery(
                "from LogGroup", LogGroup.class).getResultList();

        assertEquals(1, myLogGroups.size());
        assertTrue(myLogGroups.contains(myLogGroup_2));

        session.getTransaction().commit();
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(new MyTime(2, 2), new MyTime(3, 3), new MyTime(4, 4));
        Group myGroup = new Group("*.mp3", myRestriction);
        LogGroup myLogGroup = new LogGroup(myGroup);
        LogGroup myLogGroup_2 = new LogGroup(myGroup);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myLogGroup);
        session.save(myLogGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myLogGroup.setTimeEnd(new Date());

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogGroup> myLogGroups = session.createQuery(
                "from LogGroup", LogGroup.class).getResultList();

        int equalStartEnd = 0;
        for (LogGroup logs : myLogGroups) {
            if (logs.getTimeStart().equals(logs.getTimeEnd()))
                ++equalStartEnd;
        }

        assertEquals(1, equalStartEnd);

        session.getTransaction().commit();
    }
}
