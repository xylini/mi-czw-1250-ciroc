package pl.agh.edu.applications;

import pl.agh.edu.logs.LogApplication;
import pl.agh.edu.restrictions.Restriction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "APPLICATIONS")
public class Application implements Serializable {
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @NotNull
    @Column(name = "NAME", unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "application", orphanRemoval = true)
    private Set<LogApplication> logApplications = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "RESTRICTION_ID")
    private Restriction restriction;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    public Application() {
    }

    public Application(String name) {
        this.name = name;
    }

    public Application(String name, Restriction restriction, Group group) {
        this.name = name;
        this.restriction = restriction;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<LogApplication> getLogApplications() {
        return logApplications;
    }

    public void setLogApplications(Set<LogApplication> logApplications) {
        this.logApplications = logApplications;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void addLogApplication(LogApplication logApplication) {
        logApplication.setApplication(this);
        this.logApplications.add(logApplication);
    }

    public void removeLogApplication(LogApplication logApplication) {
        logApplication.setApplication(null);
        this.logApplications.remove(logApplication);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Application))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((Application) obj).getId())
            return false;
        else return this.name.equals(((Application) obj).getName());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
