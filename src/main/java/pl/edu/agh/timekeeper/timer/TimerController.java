package pl.edu.agh.timekeeper.timer;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.*;
import pl.edu.agh.timekeeper.windows.FocusedWindowDataExtractor;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Semaphore;

public class TimerController {
    private TimerView timerView;
    private LogApplicationDao logApplicationDaoBase;
    private ApplicationDao applicationDao;
    private final FocusedWindowDataExtractor fwde;

    private Date timeStart;
    private Date timeStop;
    private String currentWindowPath;
    private boolean isCurrentWindowRestricted;
    private long currentRestrictedAppTillNow;

    private Date prevTimeStart;
    private Date prevTimeStop;
    private String prevWindowPath;
    private boolean isPrevWindowRestricted;

    private Map<String, Date> sleepWindowDialog;
    private Map<String, Boolean> isWindowDialogShowed;
    private Semaphore windowDialogShowed;
    private int timeExceededLoop = 0;

    public TimerController(){
        this.timerView = new TimerView("00:00:00", 100, 25, -50.0, -50.0);
        this.logApplicationDaoBase = new LogApplicationDao();
        this.applicationDao = new ApplicationDao();
        this.fwde = new FocusedWindowDataExtractor();

        this.sleepWindowDialog = new HashMap<>();
        this.isWindowDialogShowed = new HashMap<>();
        this.windowDialogShowed = new Semaphore(1);
        this.timeStart = new Date(System.currentTimeMillis());
        this.timeStop = new Date(System.currentTimeMillis());
        this.currentWindowPath = fwde.getForegroundWindowPath();
        this.isCurrentWindowRestricted = isForegroundWindowRestricted();
        this.currentRestrictedAppTillNow = setCurrApplicationUsageTimeIfRestricted(this.isCurrentWindowRestricted, this.currentWindowPath);

        this.prevTimeStart = null;
        this.prevTimeStop = null;
        this.prevWindowPath = null;
        this.isPrevWindowRestricted = false;

        mainLoop(this.timerView);
        updateTimerViewTimeWorker(this.timerView);
    }

    private void mainLoop(TimerView timerView){
        Thread t = new Thread(() -> {
                try {
                    while(true){
                        Thread.sleep(16);
                        if(isCurrentWindowRestricted){
                            updateTimerViewCoords(timerView);

                        } else {
                            setNotVisible(timerView);
                        }
                        if(hasForegroundWindowChanged()){
                            prevTimeStart = timeStart;
                            prevTimeStop = timeStop;
                            isPrevWindowRestricted = isCurrentWindowRestricted;
                            prevWindowPath = currentWindowPath;

                            timeStart = new Date(System.currentTimeMillis());
                            timeStop = new Date(System.currentTimeMillis());
                            isCurrentWindowRestricted = isForegroundWindowRestricted();
                            currentWindowPath = fwde.getForegroundWindowPath();
                            currentRestrictedAppTillNow = setCurrApplicationUsageTimeIfRestricted(isCurrentWindowRestricted, currentWindowPath);
                            logIfPrevWindowRestricted(prevWindowPath, isPrevWindowRestricted, prevTimeStart, prevTimeStop);
                        }
                        if((!sleepWindowDialog.containsKey(currentWindowPath) ||
                                (sleepWindowDialog.containsKey(currentWindowPath) && (sleepWindowDialog.get(currentWindowPath)).before(timeStop)))
                                && timeExceededLoop==0 && hasTimeExceeded() && !isWindowDialogShowed.containsKey(currentWindowPath)){
                            isWindowDialogShowed.put(currentWindowPath, true);
                            Thread tDialog = new Thread(()->{
                                String tDialogPath = currentWindowPath;
                                windowDialogShowed.release();
                                Object[] options = {"1 min", "15 min", "30 min", "1 hour", "Close"};
                                Object out = JOptionPane.showInputDialog(null, "Your time has exceeded. Turn off the application or suspend for",
                                        "Time exceeded", JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                                if(out != null){
                                    String outString = (String) out;
                                    switch(outString){
                                        case "1 min":
                                            sleepWindowDialog.put(tDialogPath, new Date(System.currentTimeMillis() + 1000*60));
                                            break;
                                        case "15 min":
                                            sleepWindowDialog.put(tDialogPath, new Date(System.currentTimeMillis() + 1000*60*15));
                                            break;
                                        case "30 min":
                                            sleepWindowDialog.put(tDialogPath, new Date(System.currentTimeMillis() + 1000*60*30));
                                            break;
                                        case "1 hour":
                                            sleepWindowDialog.put(tDialogPath, new Date(System.currentTimeMillis() + 1000*60*60));
                                            break;
                                        default:
                                            closeApplication(tDialogPath);
                                            break;
                                    }
                                }
                                else{
                                    closeApplication(tDialogPath);
                                }
                                isWindowDialogShowed.remove(tDialogPath);
                            });
                            windowDialogShowed.acquire();
                            tDialog.setDaemon(true);
                            tDialog.start();
                            windowDialogShowed.acquire();
                            windowDialogShowed.release();
                        }
                        timeExceededLoop += 1;
                        timeExceededLoop %= 100; //depends on 16 millis of thread
                        timeStop.setTime(System.currentTimeMillis());
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        });
        t.setDaemon(true);
        t.start();
    }

    private void closeApplication(String path){
        String command = "(Get-WmiObject Win32_Process | Where-Object { $_.Path.StartsWith('"+path+"') }).Terminate()";
        Process powerShellProcess = null;
        try {
            powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleepWindowDialog.remove(path);
    }

    private void updateTimerViewCoords(TimerView timerView){
        double xScale = Screen.getPrimary().getOutputScaleX();
        double yScale = Screen.getPrimary().getOutputScaleY();

        double scaledMaxX = Screen.getPrimary().getBounds().getMaxX();
        double scaledMaxY = Screen.getPrimary().getBounds().getMaxY();

        double originalResolutionX = xScale * scaledMaxX;
        double originalResolutionY = yScale * scaledMaxY;

        double scaledWindowRight = fwde.getForegroundWindowRect().right / Screen.getPrimary().getOutputScaleX();
        double scaledWindowTop = fwde.getForegroundWindowRect().top / Screen.getPrimary().getOutputScaleY();

        double xShiftFactor = 248.5 / originalResolutionX;
        double yShiftFactor = 3.8 / originalResolutionY;

        double scaledRightLeftShift = -scaledMaxX * xShiftFactor * xScale;
        double scaledTopDownShift = scaledMaxY * yShiftFactor * yScale;

        double resultX = scaledWindowRight + scaledRightLeftShift;
        double resultY = scaledWindowTop + scaledTopDownShift;


        timerView.setCoordinates(resultX, resultY);
    }

    private void setNotVisible(TimerView timerView){
        timerView.setCoordinates(-100.0, -100.0);
    }

    private void updateTimerViewTimeWorker(TimerView timerView){
        Thread t = new Thread(() -> {
                try {
                    while(true){
                        Thread.sleep(500);
                        if(isCurrentWindowRestricted){
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


    private boolean isForegroundWindowRestricted(){
        boolean isRestricted = false;
        try {
            String foregroundWindowPath = this.fwde.getForegroundWindowPath();
            Optional<Application> application = applicationDao.getByPath(foregroundWindowPath);
            if (application.isPresent()) {
                Restriction restriction = application.get().getRestriction();
                if (restriction != null)
                    isRestricted = true;
            }
        } catch (IllegalStateException ex) {}
        return isRestricted;
    }

    private boolean hasForegroundWindowChanged(){
        String foregroundWindowPath = this.fwde.getForegroundWindowPath();
        boolean hasChanged = !foregroundWindowPath.equals(currentWindowPath);

        return hasChanged;
    }

    private boolean hasTimeExceeded(){
        try {
            String foregroundWindowPath = this.fwde.getForegroundWindowPath();
            Optional<Application> application = applicationDao.getByPath(foregroundWindowPath);
            if (application.isPresent()) {
                Restriction restriction = application.get().getRestriction();
                boolean hasExceeded = hasRestrictionTimeExceeded(restriction);

                Group group = application.get().getGroup();
                if(group != null){
                    Restriction groupRestriction = group.getRestriction();
                    return hasExceeded || hasRestrictionTimeExceeded(groupRestriction);
                }

                return hasExceeded;
            }
        } catch (IllegalStateException ex) {}

        return false;
    }

    private boolean hasRestrictionTimeExceeded(Restriction restriction){
        if (restriction != null){
            List<TimePair> blockedHours = restriction.getBlockedHours();
            MyTime limits = restriction.getLimit();

            for(TimePair blockedHour : blockedHours){
                if(blockedTime(blockedHour)){
                    return true;
                }
            }

            if(limits != null){
                return 60 * 60 * 1000 * limits.getHour() + 60 * 1000 * limits.getMinute() < currentRestrictedAppTillNow + (timeStop.getTime() - timeStart.getTime());
            }
        }

        return false;
    }

    private boolean blockedTime(TimePair blockedHour){
        int hourStart = blockedHour.getStart().getHour();
        int minuteStart = blockedHour.getStart().getMinute();

        int hourEnd = blockedHour.getEnd().getHour();
        int minuteEnd = blockedHour.getEnd().getMinute();

        TimeZone tz = TimeZone.getDefault();
        int minuteLocal = (int) (timeStop.getTime() / (60 * 1000) % 60);
        int hourLocal = (int) ((timeStop.getTime() + tz.getOffset(timeStop.getTime())) / (60 * 60 * 1000) % 24);

        return 60 * hourStart + minuteStart <= 60 * hourLocal + minuteLocal && 60 * hourLocal + minuteLocal < 60 * hourEnd + minuteEnd;
    }

    private void logApplicationTime(Date timeStart, Date timeStop, Application application){
        LogApplication logApplication = new LogApplication(application);
        logApplication.setTimeStart(timeStart);
        logApplication.setTimeEnd(timeStop);

        this.logApplicationDaoBase.create(logApplication);
    }

    private void logIfPrevWindowRestricted(String path, boolean isPrevWindowRestricted, Date prevStartTime, Date prevStopTime){
        if(isPrevWindowRestricted){
            Application application = applicationDao.getByPath(path).get();
            logApplicationTime(prevStartTime, prevStopTime, application);
        }
    }

    private long setCurrApplicationUsageTimeIfRestricted(boolean isCurrentWindowRestricted, String currentWindowPath){
        long timeUsage = 0;
        if(isCurrentWindowRestricted){
            Application app =  applicationDao.getByPath(currentWindowPath).get();
            timeUsage = logApplicationDaoBase.getUsageInMillisOn(LocalDate.now(), app);
        }
        return timeUsage;
    }

    private String formUsageFromMilis(long untilNow, long timeStart, long timeStop){
        long diff = untilNow + (timeStop - timeStart);
        Long seconds = diff / 1000 % 60;
        Long minutes = diff / (60 * 1000) % 60;
        Long hours = diff / (60 * 60 * 1000);

        String resultTime = hours.toString()
                            + ":" + (minutes < 10 ? "0" + minutes.toString() : minutes.toString())
                            + ":" + (seconds < 10 ? "0" + seconds.toString() : seconds.toString());

        return resultTime;
    }

}

