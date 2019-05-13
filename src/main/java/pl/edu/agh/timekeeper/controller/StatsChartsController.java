package pl.edu.agh.timekeeper.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class StatsChartsController {

    @FXML
    private Pane chartsPane;

    @FXML
    private Button todayButton;

    @FXML
    private Button lastMonthButton;

    @FXML
    private Button allTimeButton;

    @FXML
    private BarChart chart;

    @FXML
    private HBox bottomButtonsBox;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private StatsController statsController;

    @FXML
    private void initialize() {

    }

    @FXML
    private void showToday(ActionEvent actionEvent) {
    }

    @FXML
    private void showLastMonth(ActionEvent actionEvent) {
    }

    public void setStatsController(StatsController statsController) {
        this.statsController = statsController;
    }

    public void setBindings() {
        chartsPane.prefHeightProperty().bind(statsController.getStatsPane().heightProperty());
        chartsPane.prefWidthProperty().bind(statsController.getStatsPane().widthProperty()
                .subtract(statsController.getApplicationsListView().widthProperty()));
        chart.prefWidthProperty().bind(chartsPane.widthProperty());
        chart.prefHeightProperty().bind(chartsPane.heightProperty().subtract(bottomButtonsBox.getHeight()));
    }

    public void showChart() {
    }


}
