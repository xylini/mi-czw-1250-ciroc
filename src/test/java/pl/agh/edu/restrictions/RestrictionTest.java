package pl.agh.edu.restrictions;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestrictionTest {
    private Session session;
    private static Configuration configuration;

    @BeforeAll
    static void beforeAll(){
        configuration = new Configuration();
        configuration.configure("hibernate_test.cfg.xml");
        configuration.addAnnotatedClass(pl.agh.edu.applications.Application.class);
        configuration.addAnnotatedClass(pl.agh.edu.applications.Group.class);
        configuration.addAnnotatedClass(pl.agh.edu.logs.Log_App.class);
        configuration.addAnnotatedClass(pl.agh.edu.logs.Log_Group.class);
        configuration.addAnnotatedClass(pl.agh.edu.restrictions.Restriction.class);
    }

    @BeforeEach
    void beforeEach(){
        SessionFactory ourSessionFactory = configuration.buildSessionFactory();
        session = ourSessionFactory.openSession();
    }

    @AfterEach
    void afterEach(){
        session.close();
    }

    @Test
    void addTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(2,3,4);
        Restriction my_restriction_2 = new Restriction(3,4,5);
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
    void removeTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(2,3,4);
        Restriction my_restriction_2 = new Restriction(3,4,5);
        session.save(my_restriction);
        session.save(my_restriction_2);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(my_restriction);

        session.getTransaction().commit();

        List<Restriction> all_restrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList();

        assertEquals(all_restrictions.size(), 1);
        assertEquals(my_restriction_2, all_restrictions.get(0));
    }

    @Test
    void updateTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(2,3,4);
        session.save(my_restriction);

        session.getTransaction().commit();

        Restriction my_restriction_2 = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList().get(0);

        session.beginTransaction();

        my_restriction_2.setMinLimit(10);

        session.getTransaction().commit();

        Restriction my_restriction_3 = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getResultList().get(0);

        assertEquals(my_restriction, my_restriction_2);
        assertEquals(my_restriction_2, my_restriction_3);
        assertEquals(10, my_restriction_3.getMinLimit());
    }
}
