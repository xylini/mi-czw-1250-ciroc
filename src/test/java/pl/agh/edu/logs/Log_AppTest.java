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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Log_AppTest {
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

        Restriction my_restriction = new Restriction(10,12,13);
        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Log_App my_log =  new Log_App(my_application);
        Log_App my_log_2 = new Log_App(my_application);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_log);
        session.save(my_log_2);

        session.getTransaction().commit();

        session.beginTransaction();

        List<Log_App> my_logs = session.createQuery(
                "from pl.agh.edu.logs.Log_App", Log_App.class).getResultList();

        assertEquals(2, my_logs.size());
        assertTrue(my_logs.contains(my_log));
        assertTrue(my_logs.contains(my_log_2));

        session.getTransaction().commit();
    }

    @Test
    void removeTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,12,13);
        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Log_App my_log =  new Log_App(my_application);
        Log_App my_log_2 = new Log_App(my_application);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_log);
        session.save(my_log_2);

        session.getTransaction().commit();

        session.beginTransaction();

        my_application.removeLog_App(my_log);

        session.getTransaction().commit();

        session.beginTransaction();

        List<Log_App> my_logs = session.createQuery(
                "from pl.agh.edu.logs.Log_App", Log_App.class).getResultList();

        assertEquals(1, my_logs.size());
        assertTrue(my_logs.contains(my_log_2));

        session.getTransaction().commit();
    }

    @Test
    void updateTest(){
        session.beginTransaction();

        Restriction my_restriction = new Restriction(10,12,13);
        Group my_group = new Group("*.mp3", my_restriction);
        Application my_application = new Application("cos.mp3", my_restriction, my_group);
        Log_App my_log =  new Log_App(my_application);
        Log_App my_log_2 = new Log_App(my_application);

        session.save(my_restriction);
        session.save(my_group);
        session.save(my_application);
        session.save(my_log);
        session.save(my_log_2);

        session.getTransaction().commit();

        session.beginTransaction();

        my_log.settEnd(new Date());

        session.getTransaction().commit();

        session.beginTransaction();

        List<Log_App> my_logs = session.createQuery(
                "from pl.agh.edu.logs.Log_App", Log_App.class).getResultList();

        int same_start_and_end = 0;
        for(Log_App logs : my_logs){
            if(logs.gettStart().equals(logs.gettEnd()))
                ++same_start_and_end;
        }

        assertEquals(1, same_start_and_end);

        session.getTransaction().commit();
    }
}
