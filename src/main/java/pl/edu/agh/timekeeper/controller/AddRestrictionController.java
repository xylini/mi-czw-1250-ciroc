package pl.edu.agh.timekeeper.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddRestrictionController {

    @FXML
    private Tab groupTab;

    @FXML
    private Tab applicationTab;

    @FXML
    private Tab hoursBlockedTab;

    @FXML
    private Tab dailyLimitTab;

    @FXML
    private ComboBox applicationsListBox;

    @FXML
    private ComboBox groupsListBox;

    @FXML
    private VBox scrollBox;

    @FXML
    private TextField hoursDailyField;

    @FXML
    private TextField minutesDailyField;

    @FXML
    private Button okButton;

    private Button addNextHourRangeButton;


    @FXML
    private void initialize() {
        addNextHourRangeButton = new Button();
        addNextHourRangeButton.setPrefWidth(40);
        addNextHourRangeButton.setOnAction(this::addNextHourRange);
        addNextHourRange();
    }

    private void addNextHourRange(ActionEvent actionEvent) {
        addNextHourRange();
    }

    private void addNextHourRange() {
        HBox box = new HBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        List<TextField> textFields = new ArrayList<>(Arrays.asList(new TextField(), new TextField(), new TextField(), new TextField()));
        for (TextField field : textFields)
            field.setPrefSize(36, 26);
        Label toLabel = new Label("To");
        toLabel.setPadding(new Insets(0, 0, 0, 20));
        HBox spaceBox = new HBox();
        spaceBox.setPrefWidth(20);
        ImageView plusImage = new ImageView("/images/plus.png");
        plusImage.setFitHeight(15);
        plusImage.setFitWidth(15);
        addNextHourRangeButton.setGraphic(plusImage);
        box.getChildren().addAll(new Label("From"), textFields.get(0), new Label(":"), textFields.get(1), toLabel, textFields.get(2), new Label(":"), textFields.get(3), spaceBox, addNextHourRangeButton);
        scrollBox.getChildren().add(box);
    }

    @FXML
    private void okClicked(ActionEvent actionEvent) {
        // TODO Restriction restriction = new Restriction();
        if (!applicationsListBox.getSelectionModel().isEmpty() || !groupsListBox.getSelectionModel().isEmpty()) {
            if (applicationTab.isSelected()) {
                //restriction.setApplication(applicationsListBox.getValue());
            } else if (groupTab.isSelected()) {
                //restriction.setGroup(groupsListBox.getValue());
            }
            if (dailyLimitTab.isSelected()) {
                //restriction.setDailyLimit(new DailyLimit(hoursDailyField, minutesDailyField));
            } else if (hoursBlockedTab.isSelected()) {
                //restriction.setHoursLimits - create List of limits containing data from textFields
            }
        }
    }
}
