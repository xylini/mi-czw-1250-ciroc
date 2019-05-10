package pl.edu.agh.timekeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.timekeeper.windows.FocusedWindowDataExtractor;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread focusedWindowThread = new Thread(new FocusedWindowDataExtractor());
        focusedWindowThread.setDaemon(true);
        focusedWindowThread.start();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/mainView.fxml"));
        BorderPane pane = new BorderPane(loader.load());
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}