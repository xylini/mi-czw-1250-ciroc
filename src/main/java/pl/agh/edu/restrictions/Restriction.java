package pl.agh.edu.restrictions;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.agh.edu.applications.Application;
import pl.agh.edu.applications.Group;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESTRICTIONS")
public class Restriction implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @Min(0)
    @Column(name = "MIN_LIMIT")
    private int minLimit;

    @Min(0)
    @Max(24)
    @Column(name = "HOUR_START")
    private int hourStart;

    @Min(0)
    @Max(24)
    @Column(name = "HOUR_END")
    private int hourEnd;

    public Restriction() {
    }

    public Restriction(int minLimit, int hourStart, int hourEnd) {
        this.minLimit = minLimit;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
    }

    public Restriction(int minLimit, int hourStart, int hourEnd, Application application) {
        this.minLimit = minLimit;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;

        application.setRestriction(this);
    }

    public Restriction(int minLimit, int hourStart, int hourEnd, Group group) {
        this.minLimit = minLimit;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;

        group.setRestriction(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(int minLimit) {
        this.minLimit = minLimit;
    }

    public int getHourStart() {
        return hourStart;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Restriction))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((Restriction) obj).getId())
            return false;
        else if (this.minLimit != ((Restriction) obj).getMinLimit())
            return false;
        else if (this.hourStart != ((Restriction) obj).getHourStart())
            return false;
        else return this.hourEnd == ((Restriction) obj).getHourEnd();
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
