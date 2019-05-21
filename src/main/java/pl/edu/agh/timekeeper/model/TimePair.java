package pl.edu.agh.timekeeper.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "TIME_PAIRS")
public class TimePair implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name = "ID", updatable = false)
    private Integer id;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "START_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "START_MINUTE"))
    private MyTime start;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "END_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "END_MINUTE"))
    private MyTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESTRICTION_ID")
    private Restriction restriction;

    public TimePair() {
    }

    public TimePair(MyTime start, MyTime end) {
        this.start = start;
        this.end = end;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MyTime getStart() {
        return start;
    }

    public void setStart(MyTime start) {
        this.start = start;
    }

    public MyTime getEnd() {
        return end;
    }

    public void setEnd(MyTime end) {
        this.end = end;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof TimePair))
            return false;
        else if (obj == this)
            return true;
        else return this.start.getHour() == ((TimePair) obj).start.getHour()
                    && this.start.getMinute() == ((TimePair) obj).start.getMinute()
                    && this.end.getHour() == ((TimePair) obj).end.getHour()
                    && this.end.getMinute() == ((TimePair) obj).end.getMinute();
    }

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public String toString() {
        return "Start: " + start.toString() + " End: " + end.toString();
    }

}
