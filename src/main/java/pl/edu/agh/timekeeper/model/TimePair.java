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

    public MyTime getStart() {
        return start;
    }

    public MyTime getEnd() {
        return end;
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
        else return this.start.equals(((TimePair) obj).start)
                    && this.end.equals(((TimePair) obj).end);
    }

    public boolean overlapsWith(TimePair pair) {
        return (this.start.isAfter(pair.start) && pair.end.isAfter(this.start))
                || (this.end.isAfter(pair.start) && pair.end.isAfter(this.end))
                || (this.start.isAfter(pair.start) && pair.end.isAfter(this.end))
                || (pair.start.isAfter(this.start) && this.end.isAfter(pair.end));
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
