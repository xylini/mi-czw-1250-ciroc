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
        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup)
                .build();


        session.save(myRestriction);
        session.save(myGroup);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction myNewRestriction = session.createQuery(
                "from Restriction", Restriction.class).getSingleResult();

        Group myNewGroup = session.createQuery(
                "from Group", Group.class).getSingleResult();

        assertEquals(myNewRestriction, myRestriction);
        assertEquals(myNewRestriction.getLimit(), new MyTime(2, 2));
        assertEquals(myNewGroup, myGroup);

        session.getTransaction().commit();
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Group myGroup = new Group("*.mp3");
        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup)
                .build();

        Group myGroup_2 = new Group("*.wav");
        Restriction myRestriction_2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(6,40))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup_2)
                .build();

        session.save(myRestriction);
        session.save(myGroup);

        session.save(myRestriction_2);
        session.save(myGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(myGroup_2);

        myGroup.setRestriction(null);

        session.getTransaction().commit();

        session.beginTransaction();

        List<Group> myGroups = session.createQuery(
                "from Group", Group.class)
                .stream().collect(Collectors.toList());

        List<Restriction> myRestrictions = session.createQuery(
                "from Restriction", Restriction.class)
                .stream().collect(Collectors.toList());

        session.getTransaction().commit();

        assertEquals(1, myGroups.size());
        assertEquals(0, myRestrictions.size());
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Group myGroup = new Group("*.mp3");
        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup)
                .build();

        Group myGroup_2 = new Group("*.wav");
        Restriction myRestriction_2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(10,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .setGroup(myGroup_2)
                .build();

        session.save(myRestriction);
        session.save(myGroup);

        session.save(myRestriction_2);
        session.save(myGroup_2);

        session.getTransaction().commit();

        session.beginTransaction();

        myRestriction.setLimit(new MyTime(12, 2));
        myGroup_2.setName("*.txt");

        session.getTransaction().commit();

        session.beginTransaction();

        List<Restriction> myRestrictions = session.createQuery(
                "from Restriction", Restriction.class)
                .stream().filter(r -> !r.getLimit().equals(new MyTime(10, 2))).collect(Collectors.toList());

        assertEquals(1, myRestrictions.size());
        assertEquals(new MyTime(12, 2), myRestrictions.get(0).getLimit());

        List<Group> myGroups = session.createQuery(
                "from Group", Group.class)
                .stream().filter(g -> g.getName().equals("*.txt")).collect(Collectors.toList());

        assertEquals(1, myGroups.size());

        session.getTransaction();
    }
}
