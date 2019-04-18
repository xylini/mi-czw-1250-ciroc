package pl.agh.edu.restrictions;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.logs.LogApplication;
import pl.agh.edu.logs.LogGroup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestrictionTest {
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

        Restriction my_restriction = new Restriction(2, 3, 4);
        Restriction my_restriction_2 = new Restriction(3, 4, 5);
        session.save(my_restriction);
        session.save(my_restriction_2);

        session.getTransaction().commit();

        List<Restriction> all_restrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList();

        assertEquals(all_restrictions.size(), 2);
        assertEquals(my_restriction, all_restrictions.get(0));
        assertEquals(my_restriction_2, all_restrictions.get(1));
    }

    @Test
    void removeTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(2, 3, 4);
        Restriction myRestriction_2 = new Restriction(3, 4, 5);
        session.save(myRestriction);
        session.save(myRestriction_2);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(myRestriction);

        session.getTransaction().commit();

        List<Restriction> allRestrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList();

        assertEquals(allRestrictions.size(), 1);
        assertEquals(myRestriction_2, allRestrictions.get(0));
    }

    @Test
    void updateTest() {
        session.beginTransaction();

        Restriction myRestriction = new Restriction(2, 3, 4);
        session.save(myRestriction);

        session.getTransaction().commit();

        Restriction myRestriction_2 = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList().get(0);

        session.beginTransaction();

        myRestriction_2.setMinLimit(10);

        session.getTransaction().commit();

        Restriction myRestriction_3 = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList().get(0);

        assertEquals(myRestriction, myRestriction_2);
        assertEquals(myRestriction_2, myRestriction_3);
        assertEquals(10, myRestriction_3.getMinLimit());
    }
}
