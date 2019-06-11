package pl.edu.agh.timekeeper.timer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.MyTime;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.model.TimePair;
import pl.edu.agh.timekeeper.windows.FocusedWindowManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TimerController {
    private TimerView timerView;
    private LogApplicationDao logApplicationDaoBase;
    private ApplicationDao applicationDao;
    private final FocusedWindowManager fwm;

    private Date timeStart;
    private Date timeStop;
    private String currentWindowPath;
    private boolean isCurrentWindowRestricted;
    private long currentRestrictedAppTillNow;

    private Date prevTimeStart;
    private Date prevTimeStop;
    private String prevWindowPath;
    private String prevWindowText;
    private boolean isPrevWindowRestricted;

    public TimerController() {
        this.timerView = new TimerView("00:00:00", 100, 25, -50.0, -50.0);
        this.logApplicationDaoBase = new LogApplicationDao();
        this.applicationDao = new ApplicationDao();
        this.fwm = new FocusedWindowManager();

        this.timeStart = new Date(System.currentTimeMillis());
        this.timeStop = new Date(System.currentTimeMillis());
        this.currentWindowPath = fwm.getForegroundWindowPath();
        this.isCurrentWindowRestricted = isForegroundWindowRestricted();
        this.currentRestrictedAppTillNow = setCurrApplicationUsageTimeIfRestricted(this.isCurrentWindowRestricted, this.currentWindowPath);

        this.prevTimeStart = null;
        this.prevTimeStop = null;
        this.prevWindowPath = null;
        this.isPrevWindowRestricted = false;

        mainLoop(this.timerView);
        updateTimerViewTimeWorker(this.timerView);
    }

    private void closeApplication(String path) {
        String command = "wmic process where ExecutablePath='" + path.replace("\\", "\\\\") + "' delete";
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mainLoop(TimerView timerView) {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(16);
                    if (timerView.isTimerStageFocused()) {
                        fwm.focusWindow(prevWindowText);
                        continue;
                    }
                    if (isCurrentWindowRestricted) {
                        updateTimerViewCoords(timerView);
                    } else {
                        setNotVisible(timerView);
                    }
                    if (hasForegroundWindowChanged()) {
                        prevTimeStart = timeStart;
                        prevTimeStop = timeStop;
                        isPrevWindowRestricted = isCurrentWindowRestricted;
                        prevWindowPath = currentWindowPath;
                        prevWindowText = fwm.getForegroundWindowText();

                        timeStart = new Date(System.currentTimeMillis());
                        timeStop = new Date(System.currentTimeMillis());
                        isCurrentWindowRestricted = isForegroundWindowRestricted();
                        currentWindowPath = fwm.getForegroundWindowPath();
                        currentRestrictedAppTillNow = setCurrApplicationUsageTimeIfRestricted(isCurrentWindowRestricted, currentWindowPath);
                        logIfPrevWindowRestricted(prevWindowPath, isPrevWindowRestricted, prevTimeStart, prevTimeStop);
//TODO: Póki co mamy alert który ma przycisk close który ubija poprzednią apkę czyli ta na ktorej sie overtime zrobil
//                       trzeba zrobic przycisk do override i w sumie jesli nie chce sie przedlozyc albo zostawic close
//                        obrona przed zamknieciem (albo zrobic ze zamkniecie zamyka apke - to chyba drastyczne),
//                        albo zablokowac go po prostu, nie ma logiki overrida tu ale chyba Krystian cos zrobil ?
//                        nie jest skonczone sprawdzania hours range ale to w sumie mozna latwo zrobic
                        if (isCurrentWindowRestricted) {
                            Optional<Application> app = applicationDao.getByPath(currentWindowPath);
                            long timeUsage = logApplicationDaoBase.getUsageInMillisOn(LocalDate.now(), app.get());
                            if (getMillis(app.get().getRestriction().getLimit()) <= timeUsage ||
                                    isNowBlocked(app.get(), app.get().getRestriction().getBlockedHours())) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setHeaderText("Override");
                                    alert.getDialogPane().setMinSize(800, 800);
                                    alert.getDialogPane().toFront();
                                    ButtonType closeApp = new ButtonType("Close");
                                    alert.getButtonTypes().add(closeApp);
                                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                                    stage.setAlwaysOnTop(true);
                                    Optional<ButtonType> option = alert.showAndWait();

                                    if (option.get() == null) {
                                    } else if (option.get() == closeApp) {
                                        closeApplication(prevWindowPath);
                                    }
                                });
                            }
                        }


                    }
                    timeStop.setTime(System.currentTimeMillis());
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private boolean isNowBlocked(Application application, List<TimePair> blockedHours) {
        int hour = LocalTime.now().getHour();
        int minute = LocalTime.now().getMinute();
        for (TimePair timePair : blockedHours) {
            if (timePair.getStart().getHour() <= hour && hour <= timePair.getEnd().getHour())
                return true;
        }
        return false;
    }

    private void updateTimerViewCoords(TimerView timerView) {
        double xScale = Screen.getPrimary().getOutputScaleX();
        double yScale = Screen.getPrimary().getOutputScaleY();

        double scaledMaxX = Screen.getPrimary().getBounds().getMaxX();
        double scaledMaxY = Screen.getPrimary().getBounds().getMaxY();

        double originalResolutionX = xScale * scaledMaxX;
        double originalResolutionY = yScale * scaledMaxY;

        double scaledWindowRight = fwm.getForegroundWindowRect().right / Screen.getPrimary().getOutputScaleX();
        double scaledWindowTop = fwm.getForegroundWindowRect().top / Screen.getPrimary().getOutputScaleY();

        double xShiftFactor = 248.5 / originalResolutionX;
        double yShiftFactor = 3.8 / originalResolutionY;

        double scaledRightLeftShift = -scaledMaxX * xShiftFactor * xScale;
        double scaledTopDownShift = scaledMaxY * yShiftFactor * yScale;

        double resultX = scaledWindowRight + scaledRightLeftShift;
        double resultY = scaledWindowTop + scaledTopDownShift;


        timerView.setCoordinates(resultX, resultY);
    }

    private void setNotVisible(TimerView timerView) {
        timerView.setCoordinates(-100.0, -100.0);
    }

    private void updateTimerViewTimeWorker(TimerView timerView) {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(500);
                    if (isCurrentWindowRestricted) {
                        String currentTimeUsage = formUsageFromMilis(currentRestrictedAppTillNow, timeStart.getTime(), timeStop.getTime());
                        timerView.setText(currentTimeUsage);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.setDaemon(true);
        t.start();
    }


    private boolean isForegroundWindowRestricted() {
        boolean isRestricted = false;
        try {
            String foregroundWindowPath = this.fwm.getForegroundWindowPath();
            Optional<Application> application = applicationDao.getByPath(foregroundWindowPath);
            if (application.isPresent()) {
                Restriction restriction = application.get().getRestriction();
                if (restriction != null)
                    isRestricted = true;
            }
        } catch (IllegalStateException ex) {
        }

        return isRestricted;
    }

    private boolean hasForegroundWindowChanged() {
        String foregroundWindowPath = this.fwm.getForegroundWindowPath();
        boolean hasChanged = !foregroundWindowPath.equals(currentWindowPath);

        return hasChanged;
    }

    private void logApplicationTime(Date timeStart, Date timeStop, Application application) {
        LogApplication logApplication = new LogApplication(application);
        logApplication.setTimeStart(timeStart);
        logApplication.setTimeEnd(timeStop);

        this.logApplicationDaoBase.create(logApplication);
    }

    private void logIfPrevWindowRestricted(String path, boolean isPrevWindowRestricted, Date prevStartTime, Date prevStopTime) {
        if (isPrevWindowRestricted) {
            Application application = applicationDao.getByPath(path).get();
            logApplicationTime(prevStartTime, prevStopTime, application);
        }
    }

    private long setCurrApplicationUsageTimeIfRestricted(boolean isCurrentWindowRestricted, String currentWindowPath) {
        long timeUsage = 0;
        if (isCurrentWindowRestricted) {
            Application app = applicationDao.getByPath(currentWindowPath).get();
            timeUsage = logApplicationDaoBase.getUsageInMillisOn(LocalDate.now(), app);
        }
        return timeUsage;
    }

    private String formUsageFromMilis(long untilNow, long timeStart, long timeStop) {
        long diff = untilNow + (timeStop - timeStart);
        Long seconds = diff / 1000 % 60;
        Long minutes = diff / (60 * 1000) % 60;
        Long hours = diff / (60 * 60 * 1000);

        String resultTime = hours.toString()
                + ":" + (minutes < 10 ? "0" + minutes.toString() : minutes.toString())
                + ":" + (seconds < 10 ? "0" + seconds.toString() : seconds.toString());

        return resultTime;
    }

    private long getMillis(MyTime time) {
        long millis = 0;
        millis += time.getHour() * 60 * 60 * 1000;
        millis += time.getMinute() * 60 * 1000;
        return millis;
    }
}

