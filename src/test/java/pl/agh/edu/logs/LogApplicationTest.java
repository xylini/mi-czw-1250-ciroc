package pl.agh.edu.logs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.applications.Application;
import pl.agh.edu.applications.Group;
import pl.agh.edu.restrictions.Restriction;
import pl.agh.edu.restrictions.MyTime;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogApplicationTest {
    private Session session;
    private static Configuration configuration;

    @BeforeAll
    static void beforeAll() {
        configuration = new Configuration();
        configuration.configure("hibernate_test.cfg.xml");
        configuration.addAnnotatedClass(pl.agh.edu.applications.Application.class);
        configuration.addAnnotatedClass(pl.agh.edu.applications.Group.class);
        configuration.addAnnotatedClass(LogApplication.class);
        configuration.addAnnotatedClass(LogGroup.class);
        configuration.addAnnotatedClass(pl.agh.edu.restrictions.Restriction.class);
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
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);
        LogApplication myLogApplication = new LogApplication(myApplication);
        LogApplication myLogApplication_2 = new LogApplication(myApplication);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);
        session.save(myLogApplication);
        session.save(myLogApplication_2);

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogApplication> myLogApplications = session.createQuery(
                "from pl.agh.edu.logs.LogApplication", LogApplication.class).getResultList();

        assertEquals(2, myLogApplications.size());
        assertTrue(myLogApplications.contains(myLogApplication));
        assertTrue(myLogApplications.contains(myLogApplication_2));

        session.getTransaction().commit();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(new MyTime(2, 2), new MyTime(3, 3), new MyTime(4, 4));
        Group myGroup = new Group("*.mp3", myRestriction);
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);
        LogApplication myLogApplication = new LogApplication(myApplication);
        LogApplication myLogApplication_2 = new LogApplication(myApplication);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);
        session.save(myLogApplication);
        session.save(myLogApplication_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myApplication.removeLogApplication(myLogApplication);

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogApplication> myLogApplications = session.createQuery(
                "from pl.agh.edu.logs.LogApplication", LogApplication.class).getResultList();

        assertEquals(1, myLogApplications.size());
        assertTrue(myLogApplications.contains(myLogApplication_2));

        session.getTransaction().commit();
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(new MyTime(2, 2), new MyTime(3, 3), new MyTime(4, 4));
        Group myGroup = new Group("*.mp3", myRestriction);
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);
        LogApplication myLogApplication = new LogApplication(myApplication);
        LogApplication myLogApplication_2 = new LogApplication(myApplication);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);
        session.save(myLogApplication);
        session.save(myLogApplication_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myLogApplication.setTimeEnd(new Date());

        session.getTransaction().commit();

        session.beginTransaction();

        List<LogApplication> myLogApplications = session.createQuery(
                "from pl.agh.edu.logs.LogApplication", LogApplication.class).getResultList();

        int equalStartEnd = 0;
        for (LogApplication logs : myLogApplications) {
            if (logs.getTimeStart().equals(logs.getTimeEnd()))
                ++equalStartEnd;
        }

        assertEquals(1, equalStartEnd);

        session.getTransaction().commit();
    }
}
