package pl.edu.agh.timekeeper.controller;

import javafx.application.Platform;
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
import pl.edu.agh.timekeeper.db.dao.LogDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;
import pl.edu.agh.timekeeper.model.MyEntity;
import pl.edu.agh.timekeeper.model.Restriction;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private final LogDao logDao = new LogDao();

    private MyEntity entity;

    //private Application application;

    @FXML
    private void initialize() {
        xAxis.setTickLabelRotation(-45);
        chart.setAnimated(false);
        chart.prefWidthProperty().bind(chartsPane.widthProperty());
        chart.prefHeightProperty().bind(chartsPane.heightProperty().subtract(bottomButtonsBox.getHeight()));
        chart.setLegendVisible(false);
        chart.widthProperty().addListener((obs,b,b1)->{
            Platform.runLater(()->setMaxBarWidth(40, 5));
        });
    }

    public void setEntity(MyEntity entity) {
        this.entity = entity;
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
        ZonedDateTime todayAtMidnight = LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault());
        String dateStr = todayAtMidnight.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        setDescription(
                String.format("Usage of %s on %s", entity.getName(), dateStr),
                "Time",
                "Usage in minutes");

        Date todayAtMidnightDate = Date.from(todayAtMidnight.toInstant());
        LinkedHashMap<Date, Long> hourlyMillis;
        if(entity instanceof Application) hourlyMillis = logDao.getHourlyUsageInMillis((Application) entity, todayAtMidnightDate);
        else hourlyMillis = logDao.getHourlyUsageInMillis((Group) entity, todayAtMidnightDate);
        if (hourlyMillis.isEmpty()) return;
        LinkedHashMap<Date, Double> hourlySecs = new LinkedHashMap<>();
        hourlyMillis.forEach((date, value) -> hourlySecs.put(date, value / 1000.0));

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
        ZonedDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay().atZone(ZoneOffset.systemDefault());
        String dateStr = firstDayOfMonth.getMonth().name().toLowerCase();
        setDescription(
                String.format("Usage of %s in %s %d", entity.getName(), dateStr, firstDayOfMonth.getYear()),
                "Time",
                "Usage in hours");

        Date firstDayOfMonthDate = Date.from(firstDayOfMonth.toInstant());
        LinkedHashMap<Date, Long> dailyMillis;
        if(entity instanceof Application) dailyMillis = logDao.getDailyUsageInMillis((Application) entity, firstDayOfMonthDate);
        else dailyMillis = logDao.getDailyUsageInMillis((Group) entity, firstDayOfMonthDate);
        YearMonth yearMonth = YearMonth.of(firstDayOfMonth.getYear(), firstDayOfMonth.getMonth());
        LinkedHashMap<Date, Double> dailySecs = new LinkedHashMap<>();
        dailyMillis.forEach((date, value) -> dailySecs.put(date, value / 1000.0));

        XYChart.Series series = getSeries(
                yearMonth.lengthOfMonth(),
                num -> Date.from(firstDayOfMonth.plusDays(num).toInstant()),
                dailySecs,
                "dd",
                3600);

        setAxisData(series, FXCollections.observableList(IntStream.range(0, yearMonth.lengthOfMonth())
                .mapToObj(num -> formatDate(Date.from(firstDayOfMonth.plusDays(num).toInstant()), "dd"))
                .collect(Collectors.toList())));
        yAxis.setTickUnit(0.5);
    }

    //@FXML
    public void showAllTime() {
        lastMonthButton.setVisible(false);
        todayButton.setVisible(false);
        LinkedHashMap<Application, Long> totalUsage = logDao.getTotalUsageForAllEntities();
        setDescription(
                "Total usage of all applications",
                "Restriction name",
                "Usage in hours");

        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        totalUsage.keySet().stream()
                .filter(app -> app.getRestriction() != null)
                .forEach(app -> data.add(new XYChart.Data<String, Number>(app.getRestriction().getName(), totalUsage.get(app) / 3600F)));

        List<String> labels = totalUsage.keySet().stream()
                .filter(app -> app.getRestriction() != null)
                .map(Application::getRestriction)
                .map(Restriction::getName)
                .collect(Collectors.toList());
        setAxisData(series, FXCollections.observableList(labels));
        yAxis.setTickUnit(0.5);
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
            LinkedHashMap<Date, Double> usage,
            String labelPattern,
            float unitDivisor
    ) {
        XYChart.Series series = new XYChart.Series();
        ObservableList data = series.getData();
        IntStream.range(0, xAxisRange)
                .mapToObj(step::apply)
                .forEach(dateTime -> {
                    if (!usage.keySet().contains(dateTime)) {
                        usage.put(dateTime, 0D);
                        data.add(new XYChart.Data<String, Number>(formatDate(dateTime, labelPattern), 0L));
                    } else {
                        double scale = Math.pow(10, 2);
                        data.add(new XYChart.Data<String, Number>(formatDate(dateTime, labelPattern),
                                Math.round(usage.get(dateTime) / unitDivisor * scale) / scale));
                    }
                });
        return series;
    }

    private void setMaxBarWidth(double maxBarWidth, double minCategoryGap){
        double barWidth=0;
        do{
            double catSpace = xAxis.getCategorySpacing();
            double avilableBarSpace = catSpace - (chart.getCategoryGap() + chart.getBarGap());
            barWidth = (avilableBarSpace / chart.getData().size()) - chart.getBarGap();
            if (barWidth >maxBarWidth){
                avilableBarSpace=(maxBarWidth + chart.getBarGap())* chart.getData().size();
                chart.setCategoryGap(catSpace-avilableBarSpace-chart.getBarGap());
            }
        } while(barWidth>maxBarWidth);

        do{
            double catSpace = xAxis.getCategorySpacing();
            double avilableBarSpace = catSpace - (minCategoryGap + chart.getBarGap());
            barWidth = Math.min(maxBarWidth, (avilableBarSpace / chart.getData().size()) - chart.getBarGap());
            avilableBarSpace=(barWidth + chart.getBarGap())* chart.getData().size();
            chart.setCategoryGap(catSpace-avilableBarSpace-chart.getBarGap());
        } while(barWidth < maxBarWidth && chart.getCategoryGap()>minCategoryGap);
    }
}
