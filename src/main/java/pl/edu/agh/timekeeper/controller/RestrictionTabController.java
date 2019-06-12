package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.edu.agh.timekeeper.model.*;

import java.util.LinkedList;
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
    //private Label restrictionItemLabel;
    private VBox restrictionDetails;

    private Restriction restriction;

    @FXML
    private void initialize() {
    }

    public ScrollPane getRestrictionScrollPane() {
        return restrictionScrollPane;
    }

    public void setRestrictionScrollPane(ScrollPane restrictionScrollPane) {
        this.restrictionScrollPane = restrictionScrollPane;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
        refreshView();
    }

    private void refreshView() {
        restrictionNameLabel.setText(restriction.getName());
        String headerStyle = "-fx-font-size:18px; -fx-font-weight: bold;";
        String commonLabelStyle = "-fx-font-size:12px;";
        Insets headerInsets = new Insets(25, 0, 0, 0);

        refreshAppsDetails(restriction, headerInsets, headerStyle, commonLabelStyle);
        refreshLimit(restriction, headerInsets, headerStyle, commonLabelStyle);
        refreshRanges(restriction, headerInsets, headerStyle, commonLabelStyle);

        restrictionDetails.setPadding(new Insets(0, 0, 0, 20));
        restrictionDetails.setSpacing(5);
    }

    private void refreshAppsDetails(Restriction restriction, Insets headerInsets, String headerStyle, String commonLabelStyle){
        Label entityHeader = new Label();
        entityHeader.setPadding(headerInsets);
        entityHeader.setStyle(headerStyle);

        List<Label> appPaths = new LinkedList<>();
        if(restriction.getApplication() != null) {
            Application application = restriction.getApplication();
            entityHeader.setText("Application path: ");
            Label l = new Label(application.getPath());
            l.setStyle(commonLabelStyle);
            appPaths.add(l);
        } else {
            Group group = restriction.getGroup();
            entityHeader.setText("Application paths: ");
            group.getApplications().forEach(app -> {
                Label l = new Label(app.getPath());
                l.setStyle(commonLabelStyle);
                appPaths.add(l);
            });
        }
        restrictionDetails.getChildren().add(entityHeader);
        restrictionDetails.getChildren().addAll(appPaths);
    }

    private void refreshLimit(Restriction restriction, Insets headerInsets, String headerStyle, String commonLabelStyle) {
        Label limitHeader = new Label("Daily limit: ");
        limitHeader.setPadding(headerInsets);
        limitHeader.setStyle(headerStyle);

        Label limitLabel = new Label();
        limitLabel.setStyle(commonLabelStyle);
        Optional<MyTime> limit = Optional.ofNullable(restriction.getLimit());
        if(limit.isEmpty()) limitLabel.setText("-");
        else limitLabel.setText(limit.get().getHour() + " hours, " + limit.get().getMinute() + " minutes");
        restrictionDetails.getChildren().addAll(limitHeader, limitLabel);
    }

    private void refreshRanges(Restriction restriction, Insets headerInsets, String headerStyle, String commonLabelStyle) {
        Label rangeHeader = new Label("Blocked hours: ");
        rangeHeader.setPadding(headerInsets);
        List<Label> ranges = new LinkedList<>();
        rangeHeader.setStyle(headerStyle);


        List<TimePair> blockedHours = restriction.getBlockedHours();
        if (blockedHours.isEmpty()) {
            Label l = new Label("-");
            l.setStyle(commonLabelStyle);
            ranges.add(l);
        }
        blockedHours.forEach(pair -> {
            Label l = new Label("From " + pair.getStart().getHour() + ":"
                    + String.format("%02d", pair.getStart().getMinute())
                    + " to " + pair.getEnd().getHour()
                    + ":" + String.format("%02d", pair.getEnd().getMinute()));
            l.setStyle(commonLabelStyle);
            ranges.add(l);
        });
        restrictionDetails.getChildren().add(rangeHeader);
        restrictionDetails.getChildren().addAll(ranges);
    }
}
