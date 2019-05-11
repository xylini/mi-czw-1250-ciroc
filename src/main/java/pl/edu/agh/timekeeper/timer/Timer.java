package pl.edu.agh.timekeeper.timer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.model.Application;

import java.util.Optional;

public class Timer {
    private Optional<Application> currentRestrictedApplication = Optional.empty();
    private IntegerProperty secondsUsedToday = new SimpleIntegerProperty();   // prawdopodobnie property potrzebne do wyświetlania czasu na bieżąco (listener w kontrolerze widoku)
    private final ApplicationDao applicationDao = new ApplicationDao();
    private final LogApplicationDao logApplicationDao = new LogApplicationDao();

    private static final Timer instance = new Timer();

    public static Timer getInstance() {
        return instance;
    }

    public int getSecondsUsedToday() {
        return secondsUsedToday.get();
    }

    public IntegerProperty secondsUsedTodayProperty() {
        return secondsUsedToday;
    }

    public void setApplicationPath(String applicationPath) {
        if (currentRestrictedApplication.isPresent() && currentRestrictedApplication.get().getPath().equals(applicationPath))
            addSecond();
            //TODO check if restriction is not broken
        else {
            currentRestrictedApplication = Optional.of(new Application());
            currentRestrictedApplication.get().setPath(applicationPath);
            secondsUsedToday.setValue(0);
            //TODO
            // if (currentRestrictedApplication.get() in any restriction)
            //      log seconds used to database
            // Application focusedApplication = applicationDao.getByPath(applicationPath);
            // if (focusedApplication in any restriction)
            //      currentRestrictedApplication = Optional.of(focusedApplication)
            //      secondsUsedToday = logApplicationDao.get time spent today by currentRestrictedApplication
        }
    }

    private void addSecond() {
        this.secondsUsedToday.setValue(this.secondsUsedToday.get() + 1);
    }
}