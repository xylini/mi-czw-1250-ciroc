package pl.agh.edu.logs;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.agh.edu.applications.Group;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name = "LOG_GROUP")
public class Log_Group implements Serializable {
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

    @ManyToOne @NotNull
    @JoinColumn(name="GROUP_ID")
    private Group group;

    Log_Group(Group group){
        this.group = group;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Date gettStart() { return tStart; }

    public void settStart(Date tStart) { this.tStart = tStart; }

    public Date gettEnd() { return tEnd; }

    public void settEnd(Date tEnd) { this.tEnd = tEnd; }

    public Group getGroup() { return group; }

    public void setGroup(Group group) { this.group = group; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Log_Group))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Log_Group) obj).getId())
            return false;
        else if(!this.tStart.equals(((Log_Group) obj).gettStart()))
            return false;
        else if(!this.tEnd.equals(((Log_Group) obj).gettEnd()))
            return false;
        else return this.group.equals(((Log_Group) obj).getGroup());
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((tStart==null) ? 0 : tStart.hashCode());
        result = prime * result + ((tEnd==null) ? 0 : tEnd.hashCode());
        result = prime * result + ((group==null) ? 0 : group.hashCode());

        return result;
    }
}
