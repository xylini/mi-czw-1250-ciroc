package pl.edu.agh.timekeeper.db.dao;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.log.LogGroup;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.db.SessionService;

import java.util.Optional;

public abstract class DaoTestBase<D extends DaoBase<T>, T> {

    private static Configuration configuration;

    private final D dao;

    DaoTestBase(D dao) {
        this.dao = dao;
    }

    protected abstract T getEntity();

    @BeforeEach
    public void before() {
        SessionService.openSession(new Configuration()
                .configure("hibernate_test.cfg.xml")
                .addAnnotatedClass(Application.class)
                .addAnnotatedClass(Group.class)
                .addAnnotatedClass(LogApplication.class)
                .addAnnotatedClass(LogGroup.class)
                .addAnnotatedClass(Restriction.class)
                .buildSessionFactory());
    }

    @AfterEach
    public void after() {
        SessionService.closeCurrentSession();
    }

    @Test
    void createTest() {
        // given
        T entity = getEntity();
        // when
        Optional<T> createdEntity = dao.create(entity);
        // then
        Assertions.assertTrue(createdEntity.isPresent());
        Assertions.assertEquals(entity, createdEntity.get());
    }
}