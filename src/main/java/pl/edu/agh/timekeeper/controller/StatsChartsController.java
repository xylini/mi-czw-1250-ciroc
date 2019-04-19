package pl.edu.agh.timekeeper.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;

public class StatsChartsController {

    @FXML
    private Button todayButton;

    @FXML
    private Button lastWeekButton;

    @FXML
    private Button lastMonthButton;

    @FXML
    private Button allTimeButton;

    @FXML
    private BarChart chart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    //TODO private Application application; //application to retrieve usage data from it

    @FXML
    private void initialize() {

    }

//    TODO public void setApplication(){
//
//    }

    @FXML
    private void showToday(ActionEvent actionEvent) {
    }

    @FXML
    private void showLastWeek(ActionEvent actionEvent) {
    }

    @FXML
    private void showLastMonth(ActionEvent actionEvent) {
    }

    @FXML
    private void showAllTime(ActionEvent actionEvent) {
    }

    public void showChart() {
    }
}
