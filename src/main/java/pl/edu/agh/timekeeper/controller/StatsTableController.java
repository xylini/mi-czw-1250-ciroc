package pl.edu.agh.timekeeper.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StatsTableController {

    @FXML
    private TableView statsTable;

    @FXML
    private TableColumn dailyLimitColumn;

    @FXML
    private TableColumn timeSpentTodayColumn;

    @FXML
    private TableColumn overallTimeSpentColumn;

    private StatsController statsController;

    @FXML
    public void initialize() {

    }

    public void setStatsController(StatsController statsController) {
        this.statsController = statsController;
    }

    public void setBindings() {
        statsTable.prefHeightProperty().bind(statsController.getStatsPane().heightProperty());
        statsTable.prefWidthProperty().bind(statsController.getStatsPane().widthProperty()
                .subtract(statsController.getApplicationsListView().widthProperty()));
        dailyLimitColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(3.0));
        timeSpentTodayColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(3.0));
        overallTimeSpentColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(3.0));
    }

    public void setTableContent() {
        //TODO
    }
}
