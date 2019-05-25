package pl.edu.agh.timekeeper.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "RESTRICTIONS")
public class Restriction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name = "ID", updatable = false)
    private int id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Embedded
    @AttributeOverride(name = "hour", column = @Column(name = "LIMIT_HOUR"))
    @AttributeOverride(name = "minute", column = @Column(name = "LIMIT_MINUTE"))
    private MyTime limit;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "restriction", orphanRemoval = true)
    private List<TimePair> blockedHours = new ArrayList<>();

    @OneToOne(mappedBy = "restriction")
    private Application application;

    @OneToOne(mappedBy = "restriction")
    private Group group;

    public Restriction(String name, MyTime limit, Collection<TimePair> blockedHours, Application application, Group group) {
        this.name = name;
        this.limit = limit;
        this.blockedHours.addAll(blockedHours);
        this.blockedHours.forEach(pair -> pair.setRestriction(this));

        if (application != null) {
            application.setRestriction(this);
        }
        this.application = application;

        if (group != null) {
            group.setRestriction(this);
        }
        this.group = group;
    }

    public Restriction() {
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

    public MyTime getLimit() {
        return limit;
    }

    public void setLimit(MyTime limit) {
        this.limit = limit;
    }

    public List<TimePair> getBlockedHours() {
        return blockedHours;
    }

    public void setBlockedHours(List<TimePair> blockedHours) {
        this.blockedHours.clear();
        this.blockedHours.addAll(blockedHours);
        this.blockedHours.forEach(pair -> pair.setRestriction(this));
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public static RestrictionBuilder getRestrictionBuilder() {
        return new RestrictionBuilder();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof Restriction))
            return false;
        else if (obj == this)
            return true;
        else if (this.id != ((Restriction) obj).getId())
            return false;
        else if (!this.limit.equals(((Restriction) obj).getLimit()))
            return false;
        else return this.blockedHours.equals(((Restriction) obj).getBlockedHours());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
