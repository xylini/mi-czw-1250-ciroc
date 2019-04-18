package pl.agh.edu.applications;

import pl.agh.edu.logs.Log_Group;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GROUPS")
public class Group implements Serializable {
    @Id @GeneratedValue @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @NotNull
    @Column(name = "REGEX", unique = true)
    private String regex;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="group", orphanRemoval = true)
    private Set<Log_Group> log_groups = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY) @NotNull
    @JoinColumn(name="RESTRICTION_ID")
    private Restriction restriction;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="group", orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    public Group(){}

    public Group(String regex){
        this.regex = regex;
    }

    public Group(String regex, Restriction restriction){
        this.regex = regex;

        this.restriction = restriction;
        this.restriction.getGroups().add(this);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getRegex() { return regex; }

    public void setRegex(String regex) { this.regex = regex; }

    public Set<Log_Group> getLog_groups() { return log_groups; }

    public void setLog_groups(Set<Log_Group> log_groups) { this.log_groups = log_groups; }

    public Restriction getRestriction() { return restriction; }

    public void setRestriction(Restriction restriction) { this.restriction = restriction; }

    public Set<Application> getApplications() { return applications; }

    public void setApplications(Set<Application> applications) { this.applications = applications; }

    public void addApplication(Application application){
        application.setGroup(this);
        this.applications.add(application);
    }

    public void removeApplication(Application application){
        application.setGroup(null);
        this.applications.remove(application);
    }

    public void addLog_Group(Log_Group log_group){
        log_group.setGroup(this);
        this.log_groups.add(log_group);
    }

    public void removeLog_Group(Log_Group log_group){
        log_group.setGroup(null);
        this.log_groups.remove(log_group);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        else if (!(obj instanceof Group))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Group) obj).getId())
            return false;
        else return this.regex.equals(((Group) obj).getRegex());
        /*else if(!this.log_groups.equals(((Group) obj).getLog_groups()))
            return false;*/
        //else return this.restriction.equals(((Group) obj).getRestriction());
        //else return this.applications.equals(((Group) obj).getApplications());
    }

    @Override
    public int hashCode(){
        /*final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((regex==null) ? 0 : regex.hashCode());
        result = prime * result + ((log_groups==null) ? 0 : log_groups.hashCode());
        result = prime * result + ((restriction==null) ? 0 : restriction.hashCode());
        result = prime * result + ((applications==null) ? 0 : applications.hashCode());*/

        return 13;
    }
}
