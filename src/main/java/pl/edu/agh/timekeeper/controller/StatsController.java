package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Restriction;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StatsController {

    private static final String TABLE_VIEW_PATH = "/views/statsTableView.fxml";

    private static final String CHART_VIEW_PATH = "/views/statsChartsView.fxml";

    @FXML
    private Label overallLabel;

    @FXML
    private Label allTimeLabel;

    @FXML
    private Pane listPane;

    @FXML
    private BorderPane statsPane;

    @FXML
    private ListView restrictionsListView;

    private StatsChartsController statsChartsController;

    private StatsTableController statsTableController;

    private ObservableList<String> restrictionsNames = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //restrictionsListView.getItems().addAll(restrictionsNamesList);

        // TODO: remove this (test) block ---------------
        restrictionsNames.add("app1");
        restrictionsNames.add("app2");
        restrictionsListView.getItems().addAll(restrictionsNames);
        // TODO: ---------------------------------

        setCenterTable();
        setupListeners();
    }

    public BorderPane getStatsPane() {
        return statsPane;
    }

    public ListView getRestrictionsListView() {
        return restrictionsListView;
    }

    private void setupListeners() {
        restrictionsListView.getSelectionModel().selectedItemProperty().addListener((list, oldValue, newValue) -> {
            if (newValue == overallLabel) {
                setCenterTable();
            } else if (newValue == allTimeLabel) {
                setCenterChart();
                statsChartsController.showAllTime();
            } else {
                setCenterChart();
                //Restriction restriction = new RestrictionDao().getByName((String) newValue);
                //Application app = restriction.getApplication();
                new ApplicationDao().getByName((String) newValue).ifPresent(app -> {
                    statsChartsController.setApplication(app);
                    statsChartsController.showChart();
                });
            }
        });
    }

    private void setCenterTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        TableView tableView;
        try {
            tableView = loader.load();
            statsPane.setCenter(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        statsTableController = loader.getController();
        statsTableController.setStatsController(this);
        statsTableController.setRestrictions(restrictionsNames);
        statsTableController.setBindings();
    }

    private void setCenterChart() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(CHART_VIEW_PATH));
        Pane pane;
        try {
            pane = loader.load();
            statsPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        statsChartsController = loader.getController();
        statsChartsController.setStatsController(this);
        statsChartsController.setBindings();
    }
}
