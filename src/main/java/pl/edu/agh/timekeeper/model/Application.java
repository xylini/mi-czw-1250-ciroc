package pl.edu.agh.timekeeper.model;

import pl.edu.agh.timekeeper.log.LogApplication;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RESTRICTION_ID")
    private Restriction restriction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    public Application() {
    }

    public Application(String name) {
        this.name = name;
    }

    public Application(String name, Group group) {
        this.name = name;

        this.group = group;
        group.addApplication(this);
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
        if (this.restriction != null) {
            this.restriction.setApplication(null);
        }
        this.restriction = restriction;
        if (restriction != null) {
            restriction.setApplication(this);
        }
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if (this.group != null) {
            this.group.getApplications().remove(this);
        }
        this.group = group;
        if (group != null) {
            group.getApplications().add(this);
        }
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
