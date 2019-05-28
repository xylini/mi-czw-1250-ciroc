package pl.edu.agh.timekeeper.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.Duration;

public class UsageStatistics {

    private final SimpleStringProperty restrictionName;

    private final ObjectProperty<Duration> limit;

    private final ObjectProperty<Duration> todayUsage;

    private final ObjectProperty<Duration> totalUsage;

    public UsageStatistics(
            String restrictionName,
            Duration limit,
            Duration todayUsage,
            Duration totalUsage) {
        this.restrictionName = new SimpleStringProperty(restrictionName);
        this.limit = new SimpleObjectProperty<>(limit);
        this.todayUsage = new SimpleObjectProperty<>(todayUsage);
        this.totalUsage = new SimpleObjectProperty<>(totalUsage);
    }

    public String getRestrictionName() {
        return restrictionName.get();
    }

    public void setRestrictionName(String restrictionName) {
        this.restrictionName.set(restrictionName);
    }

    public SimpleStringProperty restrictionNameProperty() {
        return restrictionName;
    }

    public Duration getLimit() {
        return limit.get();
    }

    public void setLimit(Duration limit) {
        this.limit.set(limit);
    }

    public ObjectProperty<Duration> limitProperty() {
        return limit;
    }

    public Duration getTodayUsage() {
        return todayUsage.get();
    }

    public void setTodayUsage(Duration todayUsage) {
        this.todayUsage.set(todayUsage);
    }

    public ObjectProperty<Duration> todayUsageProperty() {
        return todayUsage;
    }

    public Duration getTotalUsage() {
        return totalUsage.get();
    }

    public void setTotalUsage(Duration totalUsage) {
        this.totalUsage.set(totalUsage);
    }

    public ObjectProperty<Duration> totalUsageProperty() {
        return totalUsage;
    }
}
