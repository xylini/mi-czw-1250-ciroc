package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Restriction;

import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatsTableController {

    @FXML
    private TableView<UsageStatistics> statsTable;

    @FXML
    private TableColumn<UsageStatistics, Application> restrictionName;

    @FXML
    private TableColumn<UsageStatistics, Duration> dailyLimitColumn;

    @FXML
    private TableColumn<UsageStatistics, Duration> timeSpentTodayColumn;

    @FXML
    private TableColumn<UsageStatistics, Duration> overallTimeSpentColumn;

    private ObservableList<Application> applications = FXCollections.observableArrayList();

    private ObservableList<String> restrictionNames = FXCollections.observableArrayList();

    private LinkedHashMap<Application, Long> totalUsageForAllApplications = new LinkedHashMap<>();

    private Date thisMonth;

    private Date today;

    private RestrictionDao restrictionDao = new RestrictionDao();

    private LogApplicationDao logApplicationDao = new LogApplicationDao();

    public StatsTableController() {
        ZonedDateTime monthZonedDateTime = LocalDate
                .now()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .withDayOfMonth(1);
        this.thisMonth = Date.from(monthZonedDateTime.withDayOfMonth(1).toInstant());
        ZonedDateTime todayAtMidnight = LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault());
        this.today = Date.from(todayAtMidnight.toInstant());
        logApplicationDao.getTotalUsageForAllEntities().ifPresent(usage -> totalUsageForAllApplications = usage);
    }

    @FXML
    private void initialize() {
        restrictionName.setCellValueFactory(new PropertyValueFactory<>("restrictionName"));
        dailyLimitColumn.setCellValueFactory(new PropertyValueFactory<>("limit"));
        timeSpentTodayColumn.setCellValueFactory(new PropertyValueFactory<>("todayUsage"));
        overallTimeSpentColumn.setCellValueFactory(new PropertyValueFactory<>("totalUsage"));

        dailyLimitColumn.setCellFactory(column -> getDurationTableCell());
        timeSpentTodayColumn.setCellFactory(column -> getDurationTableCell());
        overallTimeSpentColumn.setCellFactory(column -> getDurationTableCell());

        restrictionName.prefWidthProperty().bind(statsTable.widthProperty().divide(4.0));
        dailyLimitColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(4.0));
        timeSpentTodayColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(4.0));
        overallTimeSpentColumn.prefWidthProperty().bind(statsTable.widthProperty().divide(4.0));

        restrictionNames.addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String name : c.getAddedSubList()) {
                        restrictionDao.getByName(name).ifPresent(this::setTableContent);
                    }
                }
            }
        });
    }

    public TableView<UsageStatistics> getStatsTable() {
        return statsTable;
    }

    public void setApplications(Collection<Application> applications) {
        this.applications.setAll(applications);
        this.restrictionNames.setAll(applications.stream()
                .map(Application::getRestriction)
                .map(Restriction::getName)
                .collect(Collectors.toList()));
    }

    private void setTableContent(Restriction restriction) {
        Application app = restriction.getApplication();
        LinkedHashMap<Date, Long> dailyUsage = new LinkedHashMap<>();
        Optional<LinkedHashMap<Date, Long>> usage = logApplicationDao.getDailyUsageInSecs(app, thisMonth);
        if (usage.isPresent()) dailyUsage = usage.get();
        Long todayUsage = dailyUsage.getOrDefault(today, 0L);
        Long totalUsage = totalUsageForAllApplications.getOrDefault(app, 0L);
        try {
            Duration limit = Duration.ofHours(restriction.getLimit().getHour()).plusMinutes(restriction.getLimit().getMinute());
            UsageStatistics stat = new UsageStatistics(restriction.getName(), limit, secondsToLocalTime(todayUsage), secondsToLocalTime(totalUsage));
            statsTable.getItems().add(stat);
        } catch (NullPointerException e) {
        }
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
}
