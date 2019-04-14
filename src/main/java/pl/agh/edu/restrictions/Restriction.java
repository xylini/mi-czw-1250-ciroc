package pl.agh.edu.restrictions;

import pl.agh.edu.applications.Application;
import pl.agh.edu.applications.Group;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESTRICTION")
public class Restriction implements Serializable {
    @Id @GeneratedValue @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @Min(0)
    @Column(name = "MIN_LIMIT")
    private int minLimit;

    @Min(0)
    @Max(24)
    @Column(name = "HOUR_START")
    private int hourStart;

    @Min(0)
    @Max(24)
    @Column(name = "HOUR_END")
    private int hourEnd;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="restriction", orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy="restriction", orphanRemoval = true)
    private Set<Group> groups = new HashSet<>();

    Restriction(){}

    Restriction(int minLimit, int hourStart, int hourEnd){
        this.minLimit = minLimit;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getMinLimit() { return minLimit; }

    public void setMinLimit(int minLimit) { this.minLimit = minLimit; }

    public int getHourStart() { return hourStart; }

    public void setHourStart(int hourStart) { this.hourStart = hourStart; }

    public int getHourEnd() { return hourEnd; }

    public void setHourEnd(int hourEnd) { this.hourEnd = hourEnd; }

    public Set<Application> getApplications() { return applications; }

    public void setApplications(Set<Application> applications) { this.applications = applications; }

    public Set<Group> getGroups() { return groups; }

    public void setGroups(Set<Group> groups) { this.groups = groups; }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        else if (!(obj instanceof Restriction))
            return false;
        else if (obj == this)
            return true;
        else if(this.id != ((Restriction) obj).getId())
            return false;
        else if(this.minLimit != ((Restriction) obj).getMinLimit())
            return false;
        else if(this.hourStart != ((Restriction) obj).getHourStart())
            return false;
        else return this.hourEnd == ((Restriction) obj).getHourEnd();
        /*else if(!this.applications.equals(((Restriction) obj).getApplications()))
            return false;*/
        //else return this.groups.equals(((Restriction) obj).getGroups());
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + id;
        result = prime * result + minLimit;
        result = prime * result + hourStart;
        result = prime * result + hourEnd;
        //result = prime * result + ((applications==null) ? 0 : applications.hashCode());
        //result = prime * result + ((groups==null) ? 0 : groups.hashCode());

        return result;
    }
}
