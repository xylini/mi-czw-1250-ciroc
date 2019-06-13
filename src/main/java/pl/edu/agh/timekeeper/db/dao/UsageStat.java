package pl.edu.agh.timekeeper.db.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class UsageStat {

    private int year;

    private int month;

    private int day;

    private int hour;

    private long usage;

    public UsageStat() {
    }

    public UsageStat(int year, int month, int day, long usage) {
        this(year, month, day, 0, usage);
    }

    public UsageStat(int year, int month, int day, int hour, long usage) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.usage = usage;
    }

    public Date getDate() {
        return Date.from(LocalDateTime.of(year, month, day, hour, 0).atZone(ZoneId.systemDefault()).toInstant());
    }

    public long getUsage() {
        return usage;
    }
}
