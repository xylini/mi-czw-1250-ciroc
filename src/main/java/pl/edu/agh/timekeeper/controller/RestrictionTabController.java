package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.MyTime;
import pl.edu.agh.timekeeper.model.Restriction;

import java.util.Optional;

public class RestrictionTabController {
    @FXML
    private ScrollPane restrictionScrollPane;

    @FXML
    private VBox restrictionTabBox;

    @FXML
    private Label restrictionNameLabel;

    @FXML
    private Label restrictionItemLabel;

    private Restriction restriction;

    @FXML
    private void initialize() {
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
        refreshView();
    }

    public ScrollPane getRestrictionScrollPane() {
        return restrictionScrollPane;
    }

    public void setRestrictionScrollPane(ScrollPane restrictionScrollPane) {
        this.restrictionScrollPane = restrictionScrollPane;
    }

    private void refreshView() {
        restrictionNameLabel.setText(restriction.getName());
        Application application = restriction.getApplication();
        restrictionItemLabel.setText("Application path: " + application.getPath());
        Optional<MyTime> limit = Optional.ofNullable(restriction.getLimit());
        Optional<MyTime> start = Optional.ofNullable(restriction.getStart());
        Optional<MyTime> end = Optional.ofNullable(restriction.getEnd());
        limit.ifPresent(value -> {
            HBox box = new HBox();
            box.setPadding(new Insets(5));
            box.setSpacing(5);
            box.getChildren().addAll(new Label("Daily limit: " + value.getHour() + " hours, " + value.getMinute() + " minutes"));
            restrictionTabBox.getChildren().add(box);
        });
        //TODO display forbidden hours ranges
    }
}
