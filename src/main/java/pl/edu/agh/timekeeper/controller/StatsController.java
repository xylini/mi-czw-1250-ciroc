package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;

import java.io.IOException;

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
    private ListView<String> applicationsListView;

    private StatsChartsController statsChartsController;

    private StatsTableController statsTableController;

    private ObservableList<String> restrictionsNamesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        applicationsListView.setItems(restrictionsNamesList);

        // TODO: remove this (test) block ---------------
        applicationsListView.getItems().add("app1");
        applicationsListView.getItems().add("app2");
        // TODO: ---------------------------------
        //test
        ObservableList list = FXCollections.observableArrayList();
        list = applicationsListView.getItems();
        list.add("e");
        applicationsListView.setItems(list);

        setCenterTable();
        setupListeners();
    }

    public BorderPane getStatsPane() {
        return statsPane;
    }

    public ListView getApplicationsListView() {
        return applicationsListView;
    }

    private void setupListeners() {
        applicationsListView.getSelectionModel().selectedItemProperty().addListener((list, oldValue, newValue) -> {
            if (newValue == overallLabel) {
                setCenterTable();
            } else if (newValue == allTimeLabel)
                //TODO add chart with all times not ChartView
                setCenterChart();
            else {
                if (oldValue == null) setCenterChart();
                new ApplicationDao().getByName(newValue).ifPresent(app -> {
                    statsChartsController.setApplication(app);
                    statsChartsController.showToday();
                });
                setCenterChart();
                //TODO statsChartsController.setApplication(newValue)
            }
        });
    }

    private void setCenterTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        setCenter(loader);
        statsTableController = loader.getController();
        statsTableController.setRestrictions(restrictionsNamesList);
        TableView tableView;
        try {
            tableView = loader.load();
            statsPane.setCenter(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatsTableController statsTableController = loader.getController();
        statsTableController.setStatsController(this);
        statsTableController.setBindings();
    }

    private void setCenterChart() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(CHART_VIEW_PATH));
        setCenter(loader);
        statsChartsController = loader.getController();
    }

    private void setCenter(FXMLLoader loader) {
        Node node;
        Pane pane;
        try {
            pane = loader.load();
            statsPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatsChartsController statsChartsController = loader.getController();
        statsChartsController.setStatsController(this);
        statsChartsController.setBindings();
    }
}
