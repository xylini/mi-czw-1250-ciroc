package pl.agh.edu.applications;

import pl.agh.edu.logs.LogGroup;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GROUPS")
public class Group implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @NotNull
    @Column(name = "REGEX", unique = true)
    private String regex;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private Set<LogGroup> logGroups = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RESTRICTION_ID")
    private Restriction restriction;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    public Group() {
    }

    public Group(String regex) {
        this.regex = regex;
    }

    public Group(String regex, Restriction restriction) {
        this.regex = regex;

        this.restriction = restriction;
        restriction.setGroup(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Set<LogGroup> getLogGroups() {
        return logGroups;
    }

    public void setLogGroups(Set<LogGroup> logGroups) {
        this.logGroups = logGroups;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        if (this.restriction != null) {
            this.restriction.setGroup(null);
        }
        this.restriction = restriction;
        if (restriction != null) {
            restriction.setGroup(this);
        }
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public void setApplications(Set<Application> applications) {
        this.applications = applications;
    }

    public void addApplication(Application application) {
        application.setGroup(this);
        this.applications.add(application);
    }

    public void removeApplication(Application application) {
        application.setGroup(null);
        this.applications.remove(application);
    }

    public void addLogGroup(LogGroup logGroup) {
        logGroup.setGroup(this);
        this.logGroups.add(logGroup);
    }

    public void removeLogGroup(LogGroup logGroup) {
        logGroup.setGroup(null);
        this.logGroups.remove(logGroup);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Group))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((Group) obj).getId())
            return false;
        else return this.regex.equals(((Group) obj).getRegex());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
