package pl.agh.edu.applications;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.restrictions.Restriction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupTest {
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

        Restriction my_restriction = new Restriction(10,20,21);
        Group my_group = new Group("*.mp3", my_restriction);
        Group my_group_2 = new Group("*.jpg", my_restriction);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_group_2);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction my_new_restriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        assertEquals(my_new_restriction, my_restriction);
        assertEquals(my_new_restriction.getMinLimit(), 10);

        Set<Group> my_groups = my_new_restriction.getGroups();

        session.getTransaction().commit();

        assertEquals(2, my_groups.size());
        assertTrue(my_groups.contains(my_group_2));
        assertTrue(my_groups.contains(my_group));
    }

    @Test
    void removeTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,20,21);
        Group my_group = new Group("*.mp3", my_restriction);
        Group my_group_2 = new Group("*.jpg", my_restriction);

        Restriction my_restriction_2 = new Restriction(30, 10, 11);
        Group my_group_3 = new Group("*.wav", my_restriction_2);
        Group my_group_4 = new Group("*.java", my_restriction_2);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_group_2);

        session.save(my_restriction_2);
        session.save(my_group_3);
        session.save(my_group_4);

        session.getTransaction().commit();

        session.beginTransaction();

        session.delete(my_restriction);
        my_restriction_2.removeGroup(my_group_3);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction my_new_restriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        assertEquals(my_new_restriction, my_restriction_2);
        assertEquals(my_new_restriction.getMinLimit(), 30);

        Set<Group> my_groups = my_new_restriction.getGroups();

        session.getTransaction().commit();

        assertEquals(1, my_groups.size());
        assertTrue(my_groups.contains(my_group_4));
    }

    @Test
    void updateTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,20,21);
        Group my_group = new Group("*.mp3", my_restriction);
        Group my_group_2 = new Group("*.jpg", my_restriction);

        Restriction my_restriction_2 = new Restriction(30, 10, 11);
        Group my_group_3 = new Group("*.wav", my_restriction_2);
        Group my_group_4 = new Group("*.java", my_restriction_2);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_group_2);

        session.save(my_restriction_2);
        session.save(my_group_3);
        session.save(my_group_4);

        session.getTransaction().commit();

        session.beginTransaction();

        my_restriction.setMinLimit(20);
        my_group_3.setRegex("*.txt");

        session.getTransaction().commit();

        session.beginTransaction();

        List<Restriction> my_restrictions = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class)
                .stream().filter(r -> r.getMinLimit() != 30).collect(Collectors.toList());

        assertEquals(1, my_restrictions.size());
        assertEquals(20, my_restrictions.get(0).getMinLimit());

        List<Group> my_groups = session.createQuery(
                "from pl.agh.edu.applications.Group", Group.class)
                .stream().filter(g -> g.getRegex().equals("*.txt")).collect(Collectors.toList());

        assertEquals(1, my_groups.size());

        session.getTransaction();
    }
}
