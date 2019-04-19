package pl.agh.edu.restrictions;

import javax.persistence.Column;
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
}