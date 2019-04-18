package pl.agh.edu.applications;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.restrictions.Restriction;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {
    private SessionFactory ourSessionFactory;
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
        ourSessionFactory = configuration.buildSessionFactory();
        session = ourSessionFactory.openSession();
    }

    @AfterEach
    void afterEach(){
        session.close();
    }

    @Test
    void addTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,12,14);
        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Application my_application_2 = new Application("cos2.mp3", my_restriction, my_group);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_application_2);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction my_new_restriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        assertEquals(my_new_restriction.getGroups().iterator().next().getApplications().size(),
                my_new_restriction.getApplications().size());
        assertTrue(my_new_restriction.getGroups().iterator().next().getApplications()
                .containsAll(my_new_restriction.getApplications()));

        session.getTransaction();
    }

    @Test
    void removeTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,12,14);

        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Application my_application_2 = new Application("cos2.mp3", my_restriction, my_group);

        Group my_group_2 = new Group("*.jpg", my_restriction);
        Application my_application_3 = new Application("to.jpg", my_restriction, my_group_2);
        Application my_application_4 = new Application("to2.jpg", my_restriction, my_group_2);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_application_2);
        session.save(my_group_2);
        session.save(my_application_3);
        session.save(my_application_4);

        session.getTransaction().commit();

        session.beginTransaction();

        my_restriction.removeApplication(my_application);
        my_group.removeApplication(my_application);
        my_restriction.removeApplication(my_application_3);
        my_group_2.removeApplication(my_application_3);

        session.getTransaction().commit();

        session.beginTransaction();

        Restriction my_new_restriction = session.createQuery(
                "from pl.agh.edu.restrictions.Restriction", Restriction.class).getSingleResult();

        assertEquals(2, my_new_restriction.getApplications().size());

        session.getTransaction().commit();
    }

    @Test
    void updateTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,12,14);

        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Application my_application_2 = new Application("cos2.mp3", my_restriction, my_group);

        Group my_group_2 = new Group("*.jpg", my_restriction);
        Application my_application_3 = new Application("to.jpg", my_restriction, my_group_2);
        Application my_application_4 = new Application("to2.jpg", my_restriction, my_group_2);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_application_2);
        session.save(my_group_2);
        session.save(my_application_3);
        session.save(my_application_4);

        session.getTransaction().commit();

        session.beginTransaction();

        my_application_2.setName("lel.mp3");
        my_application_3.setName("lel.jpg");

        session.getTransaction().commit();

        session.beginTransaction();

        assertEquals(2, session.createQuery(
                "from pl.agh.edu.applications.Application", Application.class)
                .stream().filter(a -> a.getName().contains("lel")).count());

        session.getTransaction().commit();
    }
}
