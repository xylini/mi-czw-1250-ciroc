package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.model.Application;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;

public class StatsTableController {

    @FXML
    private TableView<UsageStatistics> statsTable;

    @FXML
    private TableColumn<UsageStatistics, Application> restrictionName;

    @FXML
    private TableColumn<UsageStatistics, Duration> limit;

    @FXML
    private TableColumn<UsageStatistics, Duration> timeToday;

    @FXML
    private TableColumn<UsageStatistics, Duration> totalTime;

    private ObservableList<String> restrictions = FXCollections.observableArrayList();

    private ApplicationDao applicationDao = new ApplicationDao();

    private LogApplicationDao logApplicationDao = new LogApplicationDao();

    private LinkedHashMap<Application, Long> totalUsageForAllApplications = new LinkedHashMap<>();

    private Date thisMonth;

    private Date today;

    public StatsTableController() {
        ZonedDateTime monthZonedDateTime = LocalDate
                .now()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .withDayOfMonth(1);
        this.thisMonth = Date.from(monthZonedDateTime.withDayOfMonth(1).toInstant());
        //TODO replace next line with this: this.today = Date.from(monthZonedDateTime.toInstant());
        this.today = Date.from(monthZonedDateTime.withDayOfMonth(10).toInstant());
        logApplicationDao.getTotalUsageForAllEntities().ifPresent(usage -> totalUsageForAllApplications = usage);
    }

    @FXML
    private TableColumn dailyLimitColumn;

    @FXML
    private TableColumn timeSpentTodayColumn;

    @FXML
    private TableColumn overallTimeSpentColumn;

    private StatsController statsController;

    @FXML
    public void initialize() {
        restrictionName.setCellValueFactory(new PropertyValueFactory<>("restrictionName"));
        limit.setCellValueFactory(new PropertyValueFactory<>("limit"));
        timeToday.setCellValueFactory(new PropertyValueFactory<>("todayUsage"));
        totalTime.setCellValueFactory(new PropertyValueFactory<>("totalUsage"));

        limit.setCellFactory(column -> getDurationTableCell());
        timeToday.setCellFactory(column -> getDurationTableCell());
        totalTime.setCellFactory(column -> getDurationTableCell());

        restrictions.addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String appName : c.getAddedSubList()) {
                        applicationDao.getByName(appName).ifPresent(this::setTableContent);
                    }
                }
            }
        });
    }

    public void setRestrictions(ObservableList<String> restrictions) {
        this.restrictions.addAll(restrictions);
    }

    private void setTableContent(Application app) {
        LinkedHashMap<Date, Long> dailyUsage = new LinkedHashMap<>();
        Optional<LinkedHashMap<Date, Long>> usage = logApplicationDao.getDailyUsageInSecs(app, thisMonth);
        if (usage.isPresent()) dailyUsage = usage.get();
        Long todayUsage = dailyUsage.getOrDefault(today, 0L);
        Long totalUsage = totalUsageForAllApplications.getOrDefault(app, 0L);
        Duration limit = Duration.ofHours(app.getRestriction().getLimit().getHour()).plusMinutes(app.getRestriction().getLimit().getMinute());
        UsageStatistics stat = new UsageStatistics(app.getName(), limit, secondsToLocalTime(todayUsage), secondsToLocalTime(totalUsage));
        statsTable.getItems().add(stat);
    }

    private Duration secondsToLocalTime(Long secs) {
        return Duration.ofSeconds(secs);
    }

    private TableCell<UsageStatistics, Duration> getDurationTableCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    long days = item.toDays();
                    long hours = item.toHours() - days * 24;
                    long minutes = item.toMinutes() - item.toHours() * 60;
                    long seconds = item.toSeconds() - item.toMinutes() * 60;
                    setText(String.format("%dd %dh %dmin %ds", days, hours, minutes, seconds));
                }
            }
        };
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
