package pl.agh.edu.logs;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.agh.edu.applications.Application;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "LOG_APPLICATIONS")
public class LogApplication implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    //@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_START")
    private Date timeStart = new Date();

    //@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_END")
    private Date timeEnd = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "APPLICATION_ID")
    private Application application;

    public LogApplication() {
    }

    public LogApplication(Application application) {
        this.application = application;
        this.application.getLogApplications().add(this);
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof LogApplication))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((LogApplication) obj).getId())
            return false;
        else if (!this.timeStart.equals(((LogApplication) obj).getTimeStart()))
            return false;
        else return this.timeEnd.equals(((LogApplication) obj).getTimeEnd());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
