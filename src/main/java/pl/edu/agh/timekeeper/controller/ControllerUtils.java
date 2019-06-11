package pl.edu.agh.timekeeper.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static pl.edu.agh.timekeeper.Main.MAIN_STAGE;

public class ControllerUtils {

    public void openWindow(FXMLLoader loader, String title) {
        try {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), 335, 480));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initOwner(MAIN_STAGE);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Button createButton(String imagePath, EventHandler<MouseEvent> eventHandler) {
        Button button = new Button();
        ImageView deleteImg = new ImageView(imagePath);
        deleteImg.setFitWidth(20);
        deleteImg.setFitHeight(20);
        button.setGraphic(deleteImg);
        button.setOnMouseClicked(eventHandler);
        return button;
    }

}
