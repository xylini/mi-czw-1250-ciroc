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

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestrictionTest {
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

        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .build();
        Restriction myRestriction_2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(3,3))
                .setStart(new MyTime(4,4))
                .setEnd(new MyTime(4,4))
                .build();

        session.save(myRestriction);
        session.save(myRestriction_2);
        session.getTransaction().commit();

        List<Restriction> all_restrictions = session.createQuery(
                "from Restriction", Restriction.class).getResultList();

        assertEquals(all_restrictions.size(), 2);
        assertEquals(myRestriction, all_restrictions.get(0));
        assertEquals(myRestriction_2, all_restrictions.get(1));
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .build();
        Restriction myRestriction_2 = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(3,3))
                .setStart(new MyTime(4,4))
                .setEnd(new MyTime(4,4))
                .build();
        session.save(myRestriction);
        session.save(myRestriction_2);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(myRestriction);

        session.getTransaction().commit();

        List<Restriction> allRestrictions = session.createQuery(
                "from Restriction", Restriction.class).getResultList();

        assertEquals(allRestrictions.size(), 1);
        assertEquals(myRestriction_2, allRestrictions.get(0));
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = Restriction.getRestrictionBuilder()
                .setLimit(new MyTime(2,2))
                .setStart(new MyTime(3,3))
                .setEnd(new MyTime(4,4))
                .build();
        session.save(myRestriction);

        session.getTransaction().commit();

        Restriction myRestriction_2 = session.createQuery(
                "from Restriction", Restriction.class).getResultList().get(0);

        session.beginTransaction();

        myRestriction_2.setLimit(new MyTime(10, 10));

        session.getTransaction().commit();

        Restriction myRestriction_3 = session.createQuery(
                "from Restriction", Restriction.class).getResultList().get(0);

        assertEquals(myRestriction, myRestriction_2);
        assertEquals(myRestriction_2, myRestriction_3);
        assertEquals(new MyTime(10, 10), myRestriction_3.getLimit());
    }
}
