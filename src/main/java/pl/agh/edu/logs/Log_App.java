package pl.agh.edu.logs;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.agh.edu.applications.Application;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "LOG_APP")
public class Log_App implements Serializable {
    @Id @GeneratedValue @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "T_START")
    private Date tStart;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "T_END")
    private Date tEnd;

    @ManyToOne(cascade = CascadeType.ALL) @NotNull
    @JoinColumn(name="APPLICATION_ID")
    private Application application;

    Log_App(){}

    Log_App(Application application){
        this.application = application;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Date gettStart() { return tStart; }

    public void settStart(Date tStart) { this.tStart = tStart; }

    public Date gettEnd() { return tEnd; }

    public void settEnd(Date tEnd) { this.tEnd = tEnd; }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Log_App))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Log_App) obj).getId())
            return false;
        else if(!this.tStart.equals(((Log_App) obj).gettStart()))
            return false;
        else return this.tEnd.equals(((Log_App) obj).gettEnd());
        //else return this.application.equals(((Log_App) obj).getApplication());
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((tStart==null) ? 0 : tStart.hashCode());
        result = prime * result + ((tEnd==null) ? 0 : tEnd.hashCode());
        //result = prime * result + ((application==null) ? 0 : application.hashCode());

        return result;
    }
}
