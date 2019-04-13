package pl.agh.edu.applications;

import pl.agh.edu.logs.Log_Group;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "GROUP")
public class Group implements Serializable {
    @Id @GeneratedValue @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @NotNull
    @Column(name = "REGEX", unique = true)
    private String regex;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="Log_Group", orphanRemoval = true)
    private Set<Log_Group> log_groups;

    @ManyToOne @NotNull
    @JoinColumn(name="RESTRICTION_ID")
    private Restriction restriction;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="Application", orphanRemoval = true)
    private Set<Application> applications;

    Group(String regex, Restriction restriction){
        this.regex = regex;
        log_groups = new HashSet<>();
        this.restriction = restriction;
        applications = new HashSet<>();
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

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        else if (!(obj instanceof Group))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Group) obj).getId())
            return false;
        else if(!this.regex.equals(((Group) obj).getRegex()))
            return false;
        /*else if(!this.log_groups.equals(((Group) obj).getLog_groups()))
            return false;*/
        else return this.restriction.equals(((Group) obj).getRestriction());
        //else return this.applications.equals(((Group) obj).getApplications());
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + ((regex==null) ? 0 : regex.hashCode());
        //result = prime * result + ((log_groups==null) ? 0 : log_groups.hashCode());
        result = prime * result + ((restriction==null) ? 0 : restriction.hashCode());
        //result = prime * result + ((applications==null) ? 0 : applications.hashCode());

        return result;
    }
}
