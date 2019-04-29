package pl.edu.agh.timekeeper.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddRestrictionController {

    @FXML
    public TextField nameRestrictionField;

    @FXML
    public HBox restrictionHBox;

    @FXML
    private RadioButton appRadioButton;

    @FXML
    private RadioButton groupRadioButton;

    @FXML
    private RadioButton filePathRadioButton;

    @FXML
    private Button addRangeButton;

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

    private ComboBox applicationsComboBox = new ComboBox();

    private ComboBox groupComboBox = new ComboBox();

    private TextField filePath = new TextField();

    private final ToggleGroup groupRadioButtons = new ToggleGroup();


    @FXML
    private void initialize() {
        appRadioButton.setToggleGroup(groupRadioButtons);
        groupRadioButton.setToggleGroup(groupRadioButtons);
        filePathRadioButton.setToggleGroup(groupRadioButtons);
        restrictionHBox.getChildren().add(applicationsComboBox);

        groupRadioButtons.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            restrictionHBox.getChildren().clear();
            if (appRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(applicationsComboBox);
            } else if (groupRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(groupComboBox);
            } else if (filePathRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(filePath);
            }
        });

    }

    @FXML
    private void okClicked(ActionEvent actionEvent) {
        restrictionsListController.restrictionListView.getItems().add(nameRestrictionField.getText());
        ((Stage) okButton.getScene().getWindow()).close();
    }

    public void setRestrictionsListController(RestrictionsListController restrictionsListController){
        this.restrictionsListController = restrictionsListController;
    }

    public void addButtonClicked(ActionEvent actionEvent) {
        int indexHBox = scrollBox.getChildren().size() - 1;
        HBox box = new HBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        List<TextField> textFields = new ArrayList<>(Arrays.asList(new TextField(), new TextField(), new TextField(), new TextField()));
        for (TextField field : textFields)
            field.setPrefSize(36, 26);
        Label toLabel = new Label("To");
        //Delete button
        Button deleteButton = new Button();
        ImageView deleteImg = new ImageView("images/delete.png");
        deleteImg.setFitWidth(25);
        deleteImg.setFitHeight(25);
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
