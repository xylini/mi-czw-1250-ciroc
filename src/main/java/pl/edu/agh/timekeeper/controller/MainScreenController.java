package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;

public class MainScreenController {

    @FXML
    public HBox menuButtonHBox;

    @FXML
    private VBox mainVBox;

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

    private static final String RESTRICTIONS_LIST_VIEW_PATH = "/views/restrictionsListView.fxml";
    private static final String STATS_VIEW_PATH = "/views/statsView.fxml";
    private static final String PREF_VIEW_PATH = "/views/prefView.fxml";
    private static final String HELP_VIEW_PATH = "/views/helpView.fxml";

    private FXMLLoader loader;

    @FXML
    private void initialize() {
        initRestrictionView();
        menuButtons.getToggles().get(0).setSelected(true);
        menuButtons.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null)
                oldValue.setSelected(true);
        }));
        restrictionsButton.prefWidthProperty().bind(mainVBox.widthProperty());
        statsButton.prefWidthProperty().bind(mainVBox.widthProperty());
        prefButton.prefWidthProperty().bind(mainVBox.widthProperty());
        helpButton.prefWidthProperty().bind(mainVBox.widthProperty());
    }

    private void initRestrictionView() {
        restrictionButtonClicked();
    }

    @FXML
    private void restrictionButtonClicked() {
        this.loader = new FXMLLoader(this.getClass().getResource(RESTRICTIONS_LIST_VIEW_PATH));
        setMainScreenContent(loader);
        prepareRestrictionView();
    }

    @FXML
    private void statsButtonClicked() {
        this.loader = new FXMLLoader(this.getClass().getResource(STATS_VIEW_PATH));
        setMainScreenContent(loader);
        prepareStatsView();
    }

    @FXML
    private void prefButtonClicked() {
        this.loader = new FXMLLoader(this.getClass().getResource(PREF_VIEW_PATH));
        setMainScreenContent(loader);
    }

    @FXML
    private void helpButtonClicked() {
        this.loader = new FXMLLoader(this.getClass().getResource(HELP_VIEW_PATH));
        setMainScreenContent(loader);
    }

    private void setMainScreenContent(FXMLLoader loader) {
        try {
            if (mainVBox.getChildren().size() > 1)
                mainVBox.getChildren().remove(1);
            mainVBox.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareRestrictionView() {
        RestrictionsListController restrictionsListController = loader.getController();
        restrictionsListController.getRestrictionsSplitPane().prefHeightProperty().bind(mainVBox.heightProperty().subtract(menuButtonHBox.heightProperty()));
        restrictionsListController.getRestrictionsSplitPane().prefWidthProperty().bind(mainVBox.widthProperty());
    }

    private void prepareStatsView() {
        StatsController statsController = loader.getController();
        statsController.getStatsBox().prefHeightProperty().bind(mainVBox.heightProperty().subtract(menuButtonHBox.heightProperty()));
        statsController.getStatsBox().prefWidthProperty().bind(mainVBox.widthProperty());
    }

}
