package pl.agh.edu.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;

public class StatsController {

    @FXML
    private Pane listPane;

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
    private void initialize() {
        //test
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("e");
        applicationsListView.setItems(list);

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
        Node node;
        try {
            node = new Pane((Pane)loader.load());
            statsPane.setCenter(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearSelections(MouseEvent mouseEvent) {
        applicationsListView.getSelectionModel().clearSelection();
    }
}
