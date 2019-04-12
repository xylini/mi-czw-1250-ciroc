package pl.agh.edu.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class StatsController {

    @FXML
    private BorderPane statsPane;

    @FXML
    private ListView applicationsListView;

    private StatsChartsController statsChartsController;

    private StatsTableController statsTableController;

    private final String TABLE_VIEW_PATH = "/views/statsTableView.fxml";

    private final String CHART_VIEW_PATH = "/views/statsChartsView.fxml";

    //TODO private something that has observable list with applications names and time usage data

    @FXML
    public void initialize() {
        // TODO applicationsListView.setItems();
        setCenterTable();
        setupListeners();
    }

    private void setupListeners() {
        applicationsListView.getSelectionModel().selectedItemProperty().addListener((list, oldValue, newValue) -> {
            if (newValue == null)
                setCenterTable();
            else {
                if (oldValue == null)
                    setCenterChart();
                //TODO statsChartsController.setApplication(newValue)
            }
        });
    }

    private void setCenterTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        statsTableController = loader.getController();
        setCenter(loader);
    }

    private void setCenterChart() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(CHART_VIEW_PATH));
        statsChartsController = loader.getController();
        setCenter(loader);
    }

    private void setCenter(FXMLLoader loader) {
        Pane pane;
        try {
            pane = new Pane(loader.load());
            statsPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
