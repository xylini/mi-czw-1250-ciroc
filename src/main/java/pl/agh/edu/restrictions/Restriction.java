package pl.agh.edu.restrictions;

import pl.agh.edu.applications.Application;
import pl.agh.edu.applications.Group;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "RESTRICTIONS")
public class Restriction implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "LIMIT_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "LIMIT_MINUTE"))
    private MyTime limit;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "START_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "START_MINUTE"))
    private MyTime start;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "END_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "END_MINUTE"))
    private MyTime end;

    @OneToOne(mappedBy = "restriction")
    private Application application;

    @OneToOne(mappedBy = "restriction")
    private Group group;

    public Restriction() {
    }

    public Restriction(MyTime limit, MyTime start, MyTime end) {
        this.limit = limit;
        this.start = start;
        this.end = end;
    }

    public Restriction(MyTime limit, MyTime start, MyTime end, Application application) {
        this.limit = limit;
        this.start = start;
        this.end = end;

        this.application = application;
        application.setRestriction(this);
    }

    public Restriction(MyTime limit, MyTime start, MyTime end, Group group) {
        this.limit = limit;
        this.start = start;
        this.end = end;

        this.group = group;
        group.setRestriction(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyTime getLimit() {
        return limit;
    }

    public void setLimit(MyTime limit) {
        this.limit = limit;
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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
        else if (!this.limit.equals(((Restriction) obj).getLimit()))
            return false;
        else if (!this.start.equals(((Restriction) obj).getStart()))
            return false;
        else return this.end.equals(((Restriction) obj).getEnd());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
