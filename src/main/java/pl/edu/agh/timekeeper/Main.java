package pl.edu.agh.timekeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.windows.FocusedWindowDataExtractor;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.MyTime;
import pl.edu.agh.timekeeper.model.Restriction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SessionService.openSession(new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory());
      
        // TODO: remove this block ---------------
        pl.edu.agh.timekeeper.model.Application app1 = new ApplicationDao().create(new pl.edu.agh.timekeeper.model.Application("app1", "app1")).get();
        pl.edu.agh.timekeeper.model.Application app2 = new ApplicationDao().create(new pl.edu.agh.timekeeper.model.Application("app2","app2")).get();
        LogApplication log1 = new LogApplication(app1);
        log1.setTimeStart(Date.from(LocalDateTime.of(2019, 5, 9, 17, 30).atZone(ZoneId.systemDefault()).toInstant()));
        log1.setTimeEnd(Date.from(LocalDateTime.of(2019, 5, 10, 9, 45).atZone(ZoneId.systemDefault()).toInstant()));
        LogApplication log2 = new LogApplication(app1);
        log2.setTimeStart(Date.from(LocalDateTime.of(2019, 4, 30, 18, 30).atZone(ZoneId.systemDefault()).toInstant()));
        log2.setTimeEnd(Date.from((LocalDateTime.of(2019, 5, 1, 19, 0).atZone(ZoneId.systemDefault()).toInstant())));
        LogApplication log3 = new LogApplication(app2);
        log3.setTimeStart(Date.from(LocalDateTime.of(2019, 5, 7, 6, 30).atZone(ZoneId.systemDefault()).toInstant()));
        log3.setTimeEnd(Date.from(LocalDateTime.of(2019, 5, 7, 9, 0).atZone(ZoneId.systemDefault()).toInstant()));
        new LogApplicationDao().create(log1);
        new LogApplicationDao().create(log2);
        new LogApplicationDao().create(log3);
        Restriction r1 = new Restriction();
        r1.setLimit(new MyTime(1,30));
        r1.setName("app1 restriction");
        Restriction r2 = new Restriction();
        r2.setLimit(new MyTime(2,45));
        r2.setName("app2 restriction");
        new RestrictionDao().create(r1);
        new RestrictionDao().create(r2);
        new ApplicationDao().addRestriction(app1,r1);
        new ApplicationDao().addRestriction(app2,r2);
        // TODO: ---------------------------------

        Thread focusedWindowThread = new Thread(new FocusedWindowDataExtractor());
        focusedWindowThread.setDaemon(true);
        focusedWindowThread.start();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/mainView.fxml"));
        BorderPane pane = new BorderPane(loader.load());
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing session");
            SessionService.closeCurrentSession();
        });
        primaryStage.show();
        primaryStage.setOnCloseRequest(val -> SessionService.closeCurrentSession());
    }
}