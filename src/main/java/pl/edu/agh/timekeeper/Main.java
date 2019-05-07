package pl.edu.agh.timekeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.log.LogGroup;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.Restriction;

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
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(val -> SessionService.closeCurrentSession());
    }
}