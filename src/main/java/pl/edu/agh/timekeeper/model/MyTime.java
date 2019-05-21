package pl.edu.agh.timekeeper.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MyTime implements Serializable {
    private int hour;
    private int minute;

    public MyTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public MyTime() {
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isAfter(MyTime other){
        return this.hour > other.hour || (this.hour == other.hour && this.minute > other.minute);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof MyTime))
            return false;
        else if (obj == this)
            return true;
        else if (this.hour != ((MyTime) obj).getHour())
            return false;
        else return this.minute == ((MyTime) obj).getMinute();
    }

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }
}
