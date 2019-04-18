package pl.agh.edu.applications;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.logs.LogApplication;
import pl.agh.edu.logs.LogGroup;
import pl.agh.edu.restrictions.Restriction;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
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

        Restriction myRestriction = new Restriction(10, 12, 14);
        Group myGroup = new Group("*.mp3", myRestriction);
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction myNewRestriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        Application myNewApplication = session.createQuery(
                "from pl.agh.edu.applications.Application", Application.class).getSingleResult();

        assertEquals(myNewRestriction, myRestriction);
        assertEquals(myNewRestriction.getMinLimit(), 10);
        assertEquals(myNewApplication, myApplication);

        session.getTransaction();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(10, 12, 14);
        Group myGroup = new Group("*.mp3", myRestriction);
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);

        session.save(myRestriction);
        session.save(myGroup);
        session.save(myApplication);

        session.getTransaction().commit();

        session.beginTransaction();

        myApplication.setRestriction(null);

        session.getTransaction().commit();

        session.beginTransaction();

        Application myNewApplication = session.createQuery(
                "from pl.agh.edu.applications.Application", Application.class).getSingleResult();

        assertNull(myNewApplication.getRestriction());
        assertEquals(myNewApplication.getGroup(), myGroup);

        session.getTransaction().commit();
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(10, 12, 14);

        Group myGroup = new Group("*.mp3", myRestriction);
        Application myApplication = new Application("cos.mp3", myRestriction, myGroup);

        Group myGroup_2 = new Group("*.jpg", myRestriction);
        Application myApplication_2 = new Application("to.jpg", myRestriction, myGroup_2);

        session.save(myRestriction);
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
                "from pl.agh.edu.applications.Application", Application.class)
                .stream().filter(a -> a.getName().contains("lel")).count());

        session.getTransaction().commit();
    }
}
