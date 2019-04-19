package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;

public class MainScreenController {

    private static final String RESTRICTIONS_LIST_VIEW_PATH = "/views/restrictionsListView.fxml";
    private static final String STATS_VIEW_PATH = "/views/statsView.fxml";
    private static final String PREF_VIEW_PATH = "/views/prefView.fxml";
    private static final String HELP_VIEW_PATH = "/views/helpView.fxml";

    @FXML
    private VBox mainVBox;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ToggleButton restrictionsButton;

    @FXML
    private ToggleButton statsButton;

    @FXML
    private ToggleButton prefButton;

    @FXML
    private ToggleButton helpButton;

    @FXML
    private ToggleGroup menuButtons;

    @FXML
    public void initialize() {
    }

    @FXML
    private void restrictionButtonClicked() {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(RESTRICTIONS_LIST_VIEW_PATH)));
    }

    @FXML
    private void statsButtonClicked() {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(STATS_VIEW_PATH)));
    }

    @FXML
    private void prefButtonClicked() {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(PREF_VIEW_PATH)));
    }

    @FXML
    private void helpButtonClicked() {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(HELP_VIEW_PATH)));
    }

    private void setContentMainBorderPane(FXMLLoader loader) {
        try {
            mainBorderPane.setLeft(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
