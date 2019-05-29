package pl.edu.agh.timekeeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.timer.TimerController;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SessionService.openSession(new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory());

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/mainView.fxml"));
        BorderPane pane = new BorderPane(loader.load());
        Scene scene = new Scene(pane);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new TimerController();
            }
        };

        Platform.runLater(runnable);

        primaryStage.setTitle("Time Keeper");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing session");
            SessionService.closeCurrentSession();
            Platform.exit();
        });
        primaryStage.show();
    }
}