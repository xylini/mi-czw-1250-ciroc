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

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {
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

        Restriction myRestriction = new Restriction(10, 20, 21);
        Group myGroup = new Group("*.mp3", myRestriction);

        session.save(myRestriction);
        session.save(myGroup);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction myNewRestriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        Group myNewGroup = session.createQuery(
                "from pl.agh.edu.applications.Group", Group.class).getSingleResult();

        assertEquals(myNewRestriction, myRestriction);
        assertEquals(myNewRestriction.getMinLimit(), 10);
        assertEquals(myNewGroup, myGroup);

        session.getTransaction().commit();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(10, 20, 21);
        Group myGroup = new Group("*.mp3", myRestriction);

        Restriction myRestriction_2 = new Restriction(30, 10, 11);
        Group myGroup_2 = new Group("*.wav", myRestriction_2);

        session.save(myRestriction);
        session.save(myGroup);

        session.save(myRestriction_2);
        session.save(myGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(myGroup_2);

        myGroup.setRestriction(null);
        session.delete(myRestriction);

        session.getTransaction().commit();

        session.beginTransaction();

        List<Group> myGroups = session.createQuery(
                "from pl.agh.edu.applications.Group", Group.class)
                .stream().collect(Collectors.toList());

        List<Restriction> myRestrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class)
                .stream().collect(Collectors.toList());

        session.getTransaction().commit();

        assertEquals(1, myGroups.size());
        assertEquals(0, myRestrictions.size());
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(10, 20, 21);
        Group myGroup = new Group("*.mp3", myRestriction);

        Restriction myRestriction_2 = new Restriction(30, 10, 11);
        Group myGroup_2 = new Group("*.wav", myRestriction_2);

        session.save(myRestriction);
        session.save(myGroup);

        session.save(myRestriction_2);
        session.save(myGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myRestriction.setMinLimit(20);
        myGroup_2.setRegex("*.txt");

        session.getTransaction().commit();

        session.beginTransaction();

        List<Restriction> myRestrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class)
                .stream().filter(r -> r.getMinLimit() != 30).collect(Collectors.toList());

        assertEquals(1, myRestrictions.size());
        assertEquals(20, myRestrictions.get(0).getMinLimit());

        List<Group> myGroups = session.createQuery(
                "from pl.agh.edu.applications.Group", Group.class)
                .stream().filter(g -> g.getRegex().equals("*.txt")).collect(Collectors.toList());

        assertEquals(1, myGroups.size());

        session.getTransaction();
    }
}
