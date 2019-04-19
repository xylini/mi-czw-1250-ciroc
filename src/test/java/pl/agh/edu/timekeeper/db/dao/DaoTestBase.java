package pl.agh.edu.timekeeper.db.dao;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.agh.edu.logs.LogApplication;
import pl.agh.edu.logs.LogGroup;
import pl.agh.edu.timekeeper.db.SessionService;

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
                .addAnnotatedClass(pl.agh.edu.applications.Application.class)
                .addAnnotatedClass(pl.agh.edu.applications.Group.class)
                .addAnnotatedClass(LogApplication.class)
                .addAnnotatedClass(LogGroup.class)
                .addAnnotatedClass(pl.agh.edu.restrictions.Restriction.class)
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