package pl.agh.edu.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.boot.jaxb.internal.stax.HbmEventReader;
import pl.agh.edu.timekeeper.Main;

import java.io.*;

public class MainScreenController {
    public ToggleGroup menuButtons;

    private final String RESTRICTIONS_LIST_VIEW_PATH = "/views/restrictionsListView.fxml";
    private final String STATS_VIEW_PATH = "/views/statsView.fxml";
    private final String PREF_VIEW_PATH = "/views/prefView.fxml";
    private final String HELP_VIEW_PATH = "/views/helpView.fxml";

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
    public void initialize() {
    }

    @FXML
    public void restrictionButtonClicked() throws IOException {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(RESTRICTIONS_LIST_VIEW_PATH)));
    }
    @FXML
    public void statsButtonClicked() throws IOException {
        setContentMainBorderPane(new FXMLLoader(this.getClass().getResource(STATS_VIEW_PATH)));
    }

    private void setContentMainBorderPane(FXMLLoader loader) {
        try {
            mainBorderPane.setLeft(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
