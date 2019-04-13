package pl.agh.edu.applications;

import pl.agh.edu.logs.Log_App;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "APPLICATION")
public class Application implements Serializable {
    @Id @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    private int id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="Log_App", orphanRemoval = true)
    private Set<Log_App> log_apps;

    @ManyToOne
    @JoinColumn(name="RESTRICTION_ID", nullable=false)
    private Restriction restriction;

    @ManyToOne
    @JoinColumn(name="GROUP_ID", nullable=false)
    private Group group;

    Application(String name, Restriction restriction, Group group){
        this.name = name;
        log_apps = new HashSet<>();
        this.restriction = restriction;
        this.group = group;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Log_App> getLog_apps() {
        return log_apps;
    }

    public void setLog_apps(Set<Log_App> log_apps) { this.log_apps = log_apps; }

    public Restriction getRestriction() { return restriction; }

    public void setRestriction(Restriction restriction) { this.restriction = restriction; }

    public Group getGroup() { return group; }

    public void setGroup(Group group) { this.group = group; }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        else if (!(obj instanceof Application))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Application) obj).getId())
            return false;
        else if(!this.name.equals(((Application) obj).getName()))
            return false;
        /*else if(!this.log_apps.equals(((Application) obj).getLog_apps()))
            return false;*/
        else if(!this.restriction.equals(((Application) obj).getRestriction()))
            return false;
        else return this.group.equals(((Application) obj).getGroup());
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((name==null) ? 0 : name.hashCode());
        //result = prime * result + ((log_apps==null) ? 0 : log_apps.hashCode());
        result = prime * result + ((restriction==null) ? 0 : restriction.hashCode());
        result = prime * result + ((group==null) ? 0 : group.hashCode());

        return result;
    }
}
