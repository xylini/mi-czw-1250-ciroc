package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import pl.edu.agh.timekeeper.db.dao.LogApplicationDao;
import pl.edu.agh.timekeeper.model.Application;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatsChartsController {

    @FXML
    private BorderPane chartsPane;

    @FXML
    private Button todayButton;

    @FXML
    private Button lastMonthButton;

    @FXML
    private BarChart<String, Number> chart;

    @FXML
    private HBox bottomButtonsBox;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private LogApplicationDao logDao;

    private Application application;

    @FXML
    private void initialize() {
        logDao = new LogApplicationDao();
        xAxis.setTickLabelRotation(-45);
        chart.setAnimated(false);
        chart.prefWidthProperty().bind(chartsPane.widthProperty());
        chart.prefHeightProperty().bind(chartsPane.heightProperty().subtract(bottomButtonsBox.getHeight()));
    }

    public void setApplication(Application app) {
        this.application = app;
    }

    public BorderPane getChartsPane() {
        return this.chartsPane;
    }

    public void showChart() {
        showToday();
    }

    @FXML
    private void showToday() {
        lastMonthButton.setVisible(true);
        todayButton.setVisible(true);
        //TODO replace next line (test) with this: ZonedDateTime todayAtMidnight = LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault());
        ZonedDateTime todayAtMidnight = LocalDate.of(2019, 5, 10).atStartOfDay().atZone(ZoneOffset.systemDefault());
        String dateStr = todayAtMidnight.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        setDescription(
                String.format("Usage of %s on %s", application.getName(), dateStr),
                "Time",
                "Usage in minutes");

        Date todayAtMidnightDate = Date.from(todayAtMidnight.toInstant());
        Optional<LinkedHashMap<Date, Long>> hsOpt = logDao.getHourlyUsageInSecs(application, todayAtMidnightDate);
        if (hsOpt.isEmpty()) return;
        LinkedHashMap<Date, Long> hourlySecs = hsOpt.get();

        XYChart.Series series = getSeries(
                24,
                num -> Date.from(todayAtMidnight.plusHours(num).toInstant()),
                hourlySecs,
                "HH:mm",
                60);
        setAxisData(series, FXCollections.observableArrayList(IntStream.range(0, 24)
                .mapToObj(num -> formatDate(Date.from(todayAtMidnight.plusHours(num).toInstant()), "HH:mm"))
                .collect(Collectors.toList())));
    }

    @FXML
    private void showLastMonth(ActionEvent actionEvent) {
        lastMonthButton.setVisible(true);
        todayButton.setVisible(true);
        //TODO replace next line (test) with this: ZonedDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay().atZone(ZoneOffset.systemDefault());
        ZonedDateTime firstDayOfMonth = LocalDate.of(2019, 5, 11).withDayOfMonth(1).atStartOfDay().atZone(ZoneOffset.systemDefault());
        String dateStr = firstDayOfMonth.getMonth().name().toLowerCase();
        setDescription(
                String.format("Usage of %s in %s %d", application.getName(), dateStr, firstDayOfMonth.getYear()),
                "Time",
                "Usage in hours");

        Date firstDayOfMonthDate = Date.from(firstDayOfMonth.toInstant());
        LinkedHashMap<Date, Long> dailySecs = logDao.getDailyUsageInSecs(application, firstDayOfMonthDate).get();
        YearMonth yearMonth = YearMonth.of(firstDayOfMonth.getYear(), firstDayOfMonth.getMonth());

        XYChart.Series series = getSeries(
                yearMonth.lengthOfMonth(),
                num -> Date.from(firstDayOfMonth.plusDays(num).toInstant()),
                dailySecs,
                "dd",
                3600);

        setAxisData(series, FXCollections.observableList(IntStream.range(0, yearMonth.lengthOfMonth())
                .mapToObj(num -> formatDate(Date.from(firstDayOfMonth.plusDays(num).toInstant()), "dd"))
                .collect(Collectors.toList())));
    }

    //@FXML
    public void showAllTime() {
        lastMonthButton.setVisible(false);
        todayButton.setVisible(false);
        Optional<LinkedHashMap<Application, Long>> totalUsage = logDao.getTotalUsageForAllEntities();
        setDescription(
                "Total usage of all applications",
                "Application",
                "Usage in hours");

        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        totalUsage.ifPresent(usage -> usage.keySet().forEach(app -> {
            data.add(new XYChart.Data<String, Number>(app.getName(), totalUsage.get().get(app) / 3600F));
        }));

        setAxisData(series, FXCollections.observableList(totalUsage.get().keySet().stream()
                .map(Application::getName)
                .collect(Collectors.toList())));
    }

    private String formatDate(Date date, String pattern) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    private void setDescription(String title, String xLabel, String yLabel) {
        chart.setTitle(title);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }

    private void setAxisData(XYChart.Series series, ObservableList<String> labels) {
        xAxis.getCategories().clear();
        xAxis.setCategories(labels);

        chart.getData().clear();
        chart.getData().add(series);

        chart.setCategoryGap(chart.getCategoryGap() / 6);
    }

    private XYChart.Series getSeries(
            int xAxisRange,
            Function<Integer, Date> step,
            LinkedHashMap<Date, Long> usage,
            String labelPattern,
            float unitDivisor
    ) {
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        IntStream.range(0, xAxisRange)
                .mapToObj(step::apply)
                .forEach(dateTime -> {
                    if (!usage.keySet().contains(dateTime)) {
                        usage.put(dateTime, 0L);
                        data.add(new XYChart.Data<String, Number>(formatDate(dateTime, labelPattern), 0L));
                    } else {
                        data.add(new XYChart.Data<String, Number>(formatDate(dateTime, labelPattern),
                                usage.get(dateTime) / unitDivisor));
                    }
                });
        return series;
    }
}
