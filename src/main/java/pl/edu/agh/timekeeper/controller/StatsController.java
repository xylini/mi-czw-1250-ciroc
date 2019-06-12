package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Restriction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private ObservableList<Application> applications = FXCollections.observableArrayList();

    private final ApplicationDao applicationDao = new ApplicationDao();

    private final RestrictionDao restrictionDao = new RestrictionDao();

    private static final String TABLE_VIEW_PATH = "/views/statsTableView.fxml";
    private static final String CHART_VIEW_PATH = "/views/statsChartsView.fxml";

    @FXML
    private void initialize() {
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
                Optional<Restriction> restriction = restrictionDao.getByName((String) newValue);
                String path = restriction.get().getApplication().getPath();
                applicationDao.getByPath(path).ifPresent(app -> {
                    statsChartsController.setApplication(app);
                    statsChartsController.showChart();
                });
            }
        });
    }

    public void setApplications(List<Application> applications) {
        this.applications.setAll(applications);
        this.statsTableController.setApplications(applications);
        ObservableList list = restrictionsListView.getItems();
        list.addAll(applications.stream()
                .filter(app -> app.getRestriction() != null)
                .map(Application::getRestriction)
                .map(Restriction::getName)
                .collect(Collectors.toList()));
        restrictionsListView.setItems(list);
    }

    private void displayTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        reloadContent(loader);
        statsTableController = loader.getController();
        statsTableController.setApplications(applications);
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
