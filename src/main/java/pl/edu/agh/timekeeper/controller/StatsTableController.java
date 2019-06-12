package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.edu.agh.timekeeper.db.dao.LogDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.MyTime;
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

    private ObservableList<String> restrictionNames = FXCollections.observableArrayList();

    private LinkedHashMap<Application, Long> totalUsageForAllApplications = new LinkedHashMap<>();

    private Date thisMonth;

    private Date today;

    private RestrictionDao restrictionDao = new RestrictionDao();

    private LogDao logDao = new LogDao();

    public StatsTableController() {
        ZonedDateTime monthZonedDateTime = LocalDate
                .now()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .withDayOfMonth(1);
        this.thisMonth = Date.from(monthZonedDateTime.withDayOfMonth(1).toInstant());
        ZonedDateTime todayAtMidnight = LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault());
        this.today = Date.from(todayAtMidnight.toInstant());
        totalUsageForAllApplications = logDao.getTotalUsageForAllEntities();
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

    public void setRestrictions(Collection<Restriction> restrictions) {
        this.restrictionNames.setAll(restrictions.stream()
                .map(Restriction::getName)
                .collect(Collectors.toList()));
    }

    private void setTableContent(Restriction restriction) {
        Long todayUsage;
        Long totalUsage;
        if(restriction.getApplication() != null) {
            Application app = restriction.getApplication();
            LinkedHashMap<Date, Long> usage = logDao.getDailyUsageInMillis(app, thisMonth);
            LinkedHashMap<Date, Long> dailyUsage = new LinkedHashMap<>();
            usage.forEach((date, value) -> dailyUsage.put(date, value / 1000));
            todayUsage = dailyUsage.getOrDefault(today, 0L);
            totalUsage = totalUsageForAllApplications.getOrDefault(app, 0L);
        } else {
            Group group = restriction.getGroup();
            LinkedHashMap<Date, Long> usage = logDao.getDailyUsageInMillis(group, thisMonth);
            LinkedHashMap<Date, Long> dailyUsage = new LinkedHashMap<>();
            usage.forEach((date, value) -> dailyUsage.put(date, value / 1000));
            todayUsage = dailyUsage.getOrDefault(today, 0L);
            totalUsage = group.getApplications().stream().mapToLong(app -> totalUsageForAllApplications.getOrDefault(app, 0L)).sum();
        }
        MyTime dailyLimit = Optional.ofNullable(restriction.getLimit()).orElse(new MyTime(24, 0));
        Duration limit = Duration.ofHours(dailyLimit.getHour()).plusMinutes(dailyLimit.getMinute());
        UsageStatistics stat = new UsageStatistics(restriction.getName(), limit, secondsToLocalTime(todayUsage), secondsToLocalTime(totalUsage));
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
}
