package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.Application;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class StatsController {

    private static final String TABLE_VIEW_PATH = "/views/statsTableView.fxml";

    private static final String CHART_VIEW_PATH = "/views/statsChartsView.fxml";

    @FXML
    private Pane listPane;

    @FXML
    private BorderPane statsPane;

    @FXML
    private ListView applicationsListView;

    private StatsChartsController statsChartsController;

    private StatsTableController statsTableController;

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
                if (oldValue == null) setCenterChart();
                // TODO: Application app = new ApplicationDao().getByName((String) newValue).get()

                // TODO: remove this (test) block ---------------
                Application app1 = new ApplicationDao().create(new Application("app1")).get();
                Application app2 = new ApplicationDao().create(new Application("app2")).get();
                LogApplication log1 = new LogApplication(app1);
                log1.setTimeStart(Date.from(LocalDateTime.of(2019, 5, 6, 17, 30).atZone(ZoneId.systemDefault()).toInstant()));
                log1.setTimeEnd(Date.from(LocalDateTime.of(2019, 5, 7, 9, 45).atZone(ZoneId.systemDefault()).toInstant()));
                LogApplication log2 = new LogApplication(app1);
                log2.setTimeStart(Date.from(LocalDateTime.of(2019, 4, 30, 18, 30).atZone(ZoneId.systemDefault()).toInstant()));
                log2.setTimeEnd(Date.from((LocalDateTime.of(2019, 5, 1, 19, 0).atZone(ZoneId.systemDefault()).toInstant())));
                LogApplication log3 = new LogApplication(app2);
                log3.setTimeStart(Date.from(LocalDateTime.of(2019, 5, 7, 6, 30).atZone(ZoneId.systemDefault()).toInstant()));
                log3.setTimeEnd(Date.from(LocalDateTime.of(2019, 5, 7, 9, 0).atZone(ZoneId.systemDefault()).toInstant()));
                new LogApplicationDao().create(log1);
                new LogApplicationDao().create(log2);
                new LogApplicationDao().create(log3);
                // TODO: ---------------------------------
                statsChartsController.setApplication(app1);
                statsChartsController.showToday();
            }
        });
    }

    private void setCenterTable() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(TABLE_VIEW_PATH));
        setCenter(loader);
        statsTableController = loader.getController();
    }

    private void setCenterChart() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(CHART_VIEW_PATH));
        setCenter(loader);
        statsChartsController = loader.getController();
    }

    private void setCenter(FXMLLoader loader) {
        Node node;
        try {
            node = new Pane((Pane) loader.load());
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
