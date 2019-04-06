package pl.agh.edu.timekeeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello world");
        Label label = new Label("Hello world");
        Scene scene = new Scene(label, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}