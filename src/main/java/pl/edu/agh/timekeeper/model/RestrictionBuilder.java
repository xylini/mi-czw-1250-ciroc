package pl.edu.agh.timekeeper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RestrictionBuilder {
    private String name;
    private MyTime limit;
    private List<TimePair> blockedHours = new ArrayList<>();
    private Application application;
    private Group group;

    public RestrictionBuilder() {
    }

    public RestrictionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public RestrictionBuilder setLimit(MyTime limit) {
        this.limit = limit;
        return this;
    }

    public RestrictionBuilder addBlockedHours(TimePair pair) {
        this.blockedHours.add(pair);
        return this;
    }

    public RestrictionBuilder addBlockedHours(Collection<TimePair> pairs) {
        this.blockedHours.addAll(pairs);
        return this;
    }

    public RestrictionBuilder setApplication(Application application) {
        this.application = application;
        return this;
    }

    public RestrictionBuilder setGroup(Group group) {
        this.group = group;
        return this;
    }

    public Restriction build() {
        return new Restriction(name, limit, blockedHours, application, group);
    }
}
