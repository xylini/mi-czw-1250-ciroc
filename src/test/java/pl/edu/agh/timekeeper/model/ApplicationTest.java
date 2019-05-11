package pl.edu.agh.timekeeper.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.log.LogGroup;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
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

        Group myGroup = new Group("*.mp3");
        Application myApplication = new Application("cos.mp3", myGroup);

        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setApplication(myApplication)
                .setGroup(myGroup)
                .build();

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction myNewRestriction = session.createQuery(
                "from Restriction", Restriction.class).getSingleResult();

        Application myNewApplication = session.createQuery(
                "from Application", Application.class).getSingleResult();

        assertEquals(myNewRestriction, myRestriction);
        assertEquals(myNewRestriction.getLimit(), new MyTime(2, 2));
        assertEquals(myNewApplication, myApplication);

        session.getTransaction();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Group myGroup = new Group("*.mp3");
        Application myApplication = new Application("cos.mp3", myGroup);
        Application myApplication_2 = new Application("nicos.mp3", myGroup);

        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2, 2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setApplication(myApplication)
                .build();
        Restriction myRestriction_2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2, 34))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup)
                .build();

        session.save(myRestriction);
        session.save(myRestriction_2);
        session.save(myGroup);
        session.save(myApplication);
        session.save(myApplication_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myApplication.setRestriction(null);
        myGroup.removeApplication(myApplication_2);

        session.getTransaction().commit();

        Application myNewApplication = session.createQuery(
                "from Application", Application.class)
                .stream().filter(a -> a.getName().equals("cos.mp3")).collect(Collectors.toList()).get(0);

        assertNull(myNewApplication.getRestriction());
        assertEquals(myNewApplication.getGroup(), myGroup);
        assertEquals(1, myGroup.getApplications().size());
        assertNull(myApplication_2.getGroup());
        assertEquals(2, session.createQuery(
                "from Application", Application.class).getResultList().size());
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Group myGroup = new Group("*.mp3");
        Application myApplication = new Application("cos.mp3", myGroup);

        Group myGroup_2 = new Group("*.jpg");
        Application myApplication_2 = new Application("to.jpg", myGroup_2);

        session.save(myGroup);
        session.save(myApplication);
        session.save(myGroup_2);
        session.save(myApplication_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myApplication.setName("lel.mp3");
        myApplication_2.setName("lel.jpg");

        session.getTransaction().commit();

        session.beginTransaction();

        assertEquals(2, session.createQuery(
                "from Application", Application.class)
                .stream().filter(a -> a.getName().contains("lel")).count());

        session.getTransaction().commit();
    }
}
