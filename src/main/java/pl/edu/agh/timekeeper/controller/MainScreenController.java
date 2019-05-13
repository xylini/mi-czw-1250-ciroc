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
    public HBox menuButtonHBox;

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
    private RestrictionsListController restrictionsListController;

    @FXML
    private FXMLLoader loader = new FXMLLoader();

    @FXML
    public void initialize() throws IOException {
        prepareRestrictionView();
        menuButtons.getToggles().get(0).setSelected(true);
        menuButtons.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null)
                oldValue.setSelected(true);
        }));
        mainBorderPane.prefHeightProperty().bind(mainVBox.heightProperty());
        mainBorderPane.prefWidthProperty().bind(mainVBox.widthProperty());
        restrictionsButton.prefWidthProperty().bind(mainVBox.widthProperty());
        statsButton.prefWidthProperty().bind(mainVBox.widthProperty());
        prefButton.prefWidthProperty().bind(mainVBox.widthProperty());
        helpButton.prefWidthProperty().bind(mainVBox.widthProperty());
    }

    private void prepareRestrictionView() throws IOException {
        setLoaderLocation(RESTRICTIONS_LIST_VIEW_PATH);
        restrictionsListController = new RestrictionsListController();
        restrictionsListController.setRestrictionsSplitPane(this.loader.load());
        restrictionsListController.setMainScreenController(this);
        restrictionsListController.setBindings();
        mainBorderPane.setTop(restrictionsListController.getRestrictionsSplitPane());
    }

    private void setLoaderLocation(String path){
        this.loader = new FXMLLoader();
        this.loader.setLocation(this.getClass().getResource(path));
    }

    public VBox getMainVBox() {
        return mainVBox;
    }

    @FXML
    private void restrictionButtonClicked() {
        mainBorderPane.setTop(restrictionsListController.getRestrictionsSplitPane());
//        restrictionsListController = loader.getController();
    }

    @FXML
    private void statsButtonClicked() {
        setLoaderLocation(STATS_VIEW_PATH);
        setContentMainBorderPane(this.loader);
    }

    @FXML
    private void prefButtonClicked() {
        setLoaderLocation(PREF_VIEW_PATH);
        setContentMainBorderPane(this.loader);
    }

    @FXML
    private void helpButtonClicked() {
        setLoaderLocation(HELP_VIEW_PATH);
        setContentMainBorderPane(this.loader);
    }

    private void setContentMainBorderPane(FXMLLoader loader) {
        try {
            mainBorderPane.setTop(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
