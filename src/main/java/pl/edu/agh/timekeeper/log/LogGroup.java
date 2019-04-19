package pl.edu.agh.timekeeper.log;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.edu.agh.timekeeper.model.Group;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "LOG_GROUPS")
public class LogGroup implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_START")
    private Date timeStart = new Date();

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_END")
    private Date timeEnd = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    public LogGroup() {
    }

    public LogGroup(Group group) {
        this.group = group;
        this.group.getLogGroups().add(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
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
        else if (!(obj instanceof LogGroup))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((LogGroup) obj).getId())
            return false;
        else if (!this.timeStart.equals(((LogGroup) obj).getTimeStart()))
            return false;
        else return this.timeEnd.equals(((LogGroup) obj).getTimeEnd());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
