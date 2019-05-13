package pl.edu.agh.timekeeper.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AddRestrictionController {

    @FXML
    private Pane mainPane;

    @FXML
    private TextField restrictionNameField;

    @FXML
    private HBox restrictionHBox;

    @FXML
    private RadioButton appRadioButton;

    @FXML
    private RadioButton groupRadioButton;

    @FXML
    private VBox scrollBox;

    @FXML
    private TextField hoursDailyField;

    @FXML
    private TextField minutesDailyField;

    @FXML
    private Button okButton;

    @FXML
    private RestrictionsListController restrictionsListController;

    private TextField applicationNameField = new TextField();

    private ComboBox groupComboBox = new ComboBox();

    private final ToggleGroup groupRadioButtons = new ToggleGroup();

    @FXML
    private void initialize() {
        appRadioButton.setToggleGroup(groupRadioButtons);
        groupRadioButton.setToggleGroup(groupRadioButtons);

        if (!restrictionHBox.getChildren().contains(applicationNameField)) {
            Button browseButton = new Button("Browse");
            browseButton.setOnAction(this::browseClicked);
            applicationNameField.setPrefSize(250, 26);
            restrictionHBox.getChildren().addAll(applicationNameField, browseButton);
        }

        groupRadioButtons.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            restrictionHBox.getChildren().clear();
            if (appRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(applicationNameField);
            } else if (groupRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(groupComboBox);
            }
        });
    }

    @FXML
    private void okClicked(ActionEvent actionEvent) {
        restrictionsListController.getRestrictionListView().getItems().add(restrictionNameField.getText());
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent((f) -> {
            applicationNameField.setText(f.getName());
            //TODO get application from database or create it, then create a restriction
        });
    }

    public void setRestrictionsListController(RestrictionsListController restrictionsListController) {
        this.restrictionsListController = restrictionsListController;
    }

    @FXML
    private void addButtonClicked(ActionEvent actionEvent) {
        int indexHBox = scrollBox.getChildren().size() - 1;
        HBox box = new HBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        List<TextField> textFields = new ArrayList<>(Arrays.asList(new TextField(), new TextField(), new TextField(), new TextField()));
        for (int index = 0; index < textFields.size(); index++) {
            textFields.get(index).setPrefSize(36, 25);
            if (index % 2 == 0) {
                textFields.get(index).setPromptText("HH");
            } else {
                textFields.get(index).setPromptText("MM");
            }
        }
        Label toLabel = new Label("To");
        //Delete button
        Button deleteButton = new Button();
        ImageView deleteImg = new ImageView("images/delete.png");
        deleteImg.setFitWidth(20);
        deleteImg.setFitHeight(20);
        deleteButton.setGraphic(deleteImg);
        deleteButton.setOnMouseClicked(deleteEvent);
        //
        toLabel.setPadding(new Insets(0, 0, 0, 20));
        box.getChildren().addAll(new Label("From"), textFields.get(0), new Label(":"), textFields.get(1), toLabel, textFields.get(2), new Label(":"), textFields.get(3), deleteButton);
        scrollBox.getChildren().add(indexHBox, box);
    }

    private EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(final MouseEvent ME) {
            Object button = ME.getSource();
            if (button instanceof Button) {
                scrollBox.getChildren().remove(((Button) button).getParent());
            }
        }
    };
}
