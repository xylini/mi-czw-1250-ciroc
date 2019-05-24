package pl.edu.agh.timekeeper.timer;

import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.w3c.dom.css.Rect;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.model.Application;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

public class Timer {
    private HashMap<String, IntegerProperty> applicationTimeHashMap = new HashMap<>();
    private HashMap<String, Optional<Application>> applicationsMap = new HashMap<>();

    private Optional<Application> currentRestrictedApplication = Optional.empty();
    private IntegerProperty secondsUsedToday = new SimpleIntegerProperty();   // prawdopodobnie property potrzebne do wyświetlania czasu na bieżąco (listener w kontrolerze widoku)
    private final ApplicationDao applicationDao = new ApplicationDao();
    private final LogApplicationDao logApplicationDao = new LogApplicationDao();
    private Integer dayOfYear = Calendar.DAY_OF_YEAR;
    private WinDef.RECT foregroundWindowRect = null;

//    Thread viewThread = new Thread(new TimerView());

    private static final Timer instance = new Timer();

    public void StartTimerView(WinDef.RECT foregroundWindowRect){
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                Text text = new Text("0:00");
                Stage primaryStage = new Stage();
                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(text);
                primaryStage.initStyle(StageStyle.UNDECORATED);
                primaryStage.setAlwaysOnTop(true);
                primaryStage.setScene(new Scene(borderPane, 100, 25));
                primaryStage.show();
                Thread timerView = new Thread(){
                    public void run() {
                        try{
                            while(true){
                                Thread.sleep(1000);
                                Rectangle2D screen = Screen.getPrimary().getBounds();
                                text.setText(getInstance().getCurrentWindowUsageTime());
                                Double timerLeftShift = (screen.getMaxX()-screen.getMinX())*0.165;
                                primaryStage.setX(foregroundWindowRect.right/primaryStage.getOutputScaleX() - screen.getMaxX()*0.129*screen.getMaxX()/1080*primaryStage.getOutputScaleX()); // ok
                                primaryStage.setY(foregroundWindowRect.top/primaryStage.getOutputScaleY() + (screen.getMaxY()*0.0035)*primaryStage.getOutputScaleY()); // ok
                            }
                        } catch (InterruptedException e) {
                            //asd
                        }
                    }
                };
                timerView.setDaemon(true);
                timerView.start();
            }
        };
        javafx.application.Platform.runLater(runnable);
    }



    public static Timer getInstance() {
        return instance;
    }

    public int getCurrentProgramSeconds(){
        return this.applicationTimeHashMap.get(this.currentRestrictedApplication.get().getPath()).get();
    }

    public String getCurrentWindowUsageTime(){
        Integer minutes = (getCurrentProgramSeconds() / 60) % 60;
        Integer hours = getCurrentProgramSeconds() / 3600;
        String textTime = hours.toString() + ":" + (minutes < 10 ? "0" + minutes.toString() : minutes.toString());

        return textTime;
    }

    public IntegerProperty getCurrentProgramSecondsProperty() {
        return secondsUsedToday;
    }

    public void setApplicationPath(String applicationPath, WinDef.RECT foregroundWindowRect) {
        this.checkIfDayChanged();
        if (this.currentRestrictedApplication.isPresent() && this.currentRestrictedApplication.get().getPath().equals(applicationPath)){
            addSecond(this.currentRestrictedApplication.get());
        }
        else if(!this.applicationTimeHashMap.containsKey(applicationPath)) {
            Optional<Application> newApplication = Optional.of(new Application());
            newApplication.get().setPath(applicationPath);
            this.currentRestrictedApplication = newApplication;
            this.applicationTimeHashMap.put(applicationPath, new SimpleIntegerProperty(1));
            this.applicationsMap.put(applicationPath, newApplication);
        }
        else if(!this.currentRestrictedApplication.get().getPath().equals(applicationPath) && this.applicationTimeHashMap.containsKey(applicationPath)){
            this.currentRestrictedApplication = applicationsMap.get(applicationPath);
            addSecond(this.currentRestrictedApplication.get());

            //TODO
            // if (currentRestrictedApplication.get() in any restriction)
            //      log seconds used to database
            // Application focusedApplication = applicationDao.getByPath(applicationPath);
            // if (focusedApplication in any restriction)
            //      currentRestrictedApplication = Optional.of(focusedApplication)
            //      secondsUsedToday = logApplicationDao.get time spent today by currentRestrictedApplication
        }
    }

    private void addSecond(Application application) {
        IntegerProperty to = this.applicationTimeHashMap.get(application.getPath());
        to.setValue(to.get()+1);
    }

    private void checkIfDayChanged(){
        if(this.dayOfYear != Calendar.DAY_OF_YEAR){
            this.applicationTimeHashMap.values().forEach(integerProperty -> integerProperty.setValue(1));
            this.dayOfYear = Calendar.DAY_OF_YEAR;
        }
    }
}