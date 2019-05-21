package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.edu.agh.timekeeper.model.*;

import java.util.List;
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
        List<TimePair> blockedHours = restriction.getBlockedHours();
        limit.ifPresent(value -> {
            HBox box = new HBox();
            box.setPadding(new Insets(5));
            box.setSpacing(5);
            box.getChildren().add(new Label("Daily limit: " + value.getHour() + " hours, " + value.getMinute() + " minutes"));
            restrictionTabBox.getChildren().add(box);
        });
        HBox headerBox = new HBox();
        if (!blockedHours.isEmpty()) {
            headerBox.setPadding(new Insets(5));
            headerBox.setSpacing(5);
            headerBox.getChildren().add(new Label("Blocked hours: "));
            restrictionTabBox.getChildren().add(headerBox);
        }
        blockedHours.forEach(pair -> {
            HBox box = new HBox();
            box.setPadding(new Insets(5));
            box.setSpacing(5);
            box.getChildren().add(new Label("From: " + pair.getStart().getHour() + ":" + pair.getStart().getMinute()
                    + " to " + pair.getEnd().getHour() + ":" + pair.getEnd().getMinute()));
            restrictionTabBox.getChildren().add(box);
        });
    }
}
