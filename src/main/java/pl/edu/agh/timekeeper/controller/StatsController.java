package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;

import java.io.IOException;

public class StatsController {

    @FXML
    private Label overallLabel;

    @FXML
    private Label allTimeLabel;

    @FXML
    private HBox statsBox;

    @FXML
    private ListView restrictionsListView;

    private StatsChartsController statsChartsController;

    private StatsTableController statsTableController;

    private ObservableList<String> restrictionsNames = FXCollections.observableArrayList();

    private static final String TABLE_VIEW_PATH = "/views/statsTableView.fxml";

    private static final String CHART_VIEW_PATH = "/views/statsChartsView.fxml";

    @FXML
    private void initialize() {
        // TODO: remove this (test) block ---------------
        restrictionsNames.add("app1");
        restrictionsNames.add("app2");
        restrictionsListView.getItems().addAll(restrictionsNames);
        // TODO: ---------------------------------

        displayTable();
        restrictionsListView.prefHeightProperty().bind(statsBox.heightProperty());
        setupListeners();
    }

    public HBox getStatsBox() {
        return statsBox;
    }

    private void setupListeners() {
        restrictionsListView.getSelectionModel().selectedItemProperty().addListener((list, oldValue, newValue) -> {
            if (newValue == overallLabel) {
                displayTable();
            } else if (newValue == allTimeLabel) {
                displayChart();
                statsChartsController.showAllTime();
            } else {
                displayChart();
                new ApplicationDao().getByName((String) newValue).ifPresent(app -> {
                    statsChartsController.setApplication(app);
                    statsChartsController.showChart();
                });
            }
        });
    }

    private void displayTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        reloadContent(loader);
        statsTableController = loader.getController();
        statsTableController.setRestrictions(restrictionsNames);
        statsTableController.getStatsTable().prefHeightProperty().bind(statsBox.heightProperty());
        statsTableController.getStatsTable().prefWidthProperty().bind(statsBox.widthProperty().subtract(restrictionsListView.widthProperty()));
    }

    private void displayChart() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(CHART_VIEW_PATH));
        reloadContent(loader);
        statsChartsController = loader.getController();
        statsChartsController.getChartsPane().prefHeightProperty().bind(statsBox.heightProperty());
        statsChartsController.getChartsPane().prefWidthProperty().bind(statsBox.widthProperty().subtract(restrictionsListView.widthProperty()));
    }

    private void reloadContent(FXMLLoader loader) {
        try {
            if (statsBox.getChildren().size() > 1)
                statsBox.getChildren().remove(1);
            statsBox.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
