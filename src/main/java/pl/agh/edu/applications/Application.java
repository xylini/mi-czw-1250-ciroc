package pl.agh.edu.applications;

import pl.agh.edu.logs.Log_App;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "APPLICATIONS")
public class Application implements Serializable {
    @Id @GeneratedValue @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @NotNull
    @Column(name = "NAME", unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="application", orphanRemoval = true)
    private Set<Log_App> log_apps = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY) @NotNull
    @JoinColumn(name="RESTRICTION_ID")
    private Restriction restriction;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull
    @JoinColumn(name="GROUP_ID")
    private Group group;

    public Application(){}

    public Application(String name){
        this.name = name;
    }

    public Application(String name, Restriction restriction, Group group){
        this.name = name;

        this.restriction = restriction;
        this.restriction.getApplications().add(this);

        this.group = group;
        this.group.getApplications().add(this);
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

    public void addLog_App(Log_App log_app){
        log_app.setApplication(this);
        this.log_apps.add(log_app);
    }

    public void removeLog_App(Log_App log_app){
        log_app.setApplication(null);
        this.log_apps.remove(log_app);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        else if (!(obj instanceof Application))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Application) obj).getId())
            return false;
        else return this.name.equals(((Application) obj).getName());
        /*else if(!this.log_apps.equals(((Application) obj).getLog_apps()))
            return false;*/
        /*else if(!this.restriction.equals(((Application) obj).getRestriction()))
            return false;*/
        //else return this.group.equals(((Application) obj).getGroup());
    }

    @Override
    public int hashCode(){
        /*final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((name==null) ? 0 : name.hashCode());
        result = prime * result + ((log_apps==null) ? 0 : log_apps.hashCode());
        result = prime * result + ((restriction==null) ? 0 : restriction.hashCode());
        result = prime * result + ((group==null) ? 0 : group.hashCode());*/

        return 13;
    }
}
