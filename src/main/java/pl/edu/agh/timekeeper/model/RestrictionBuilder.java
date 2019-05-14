package pl.edu.agh.timekeeper.model;

public class RestrictionBuilder {
    private String name;
    private MyTime limit;
    private MyTime start;
    private MyTime end;
    private Application application;
    private Group group;

    public RestrictionBuilder(){}

    public RestrictionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public RestrictionBuilder setLimit(MyTime limit) {
        this.limit = limit;
        return this;
    }

    public RestrictionBuilder setStart(MyTime start) {
        this.start = start;
        return this;
    }

    public RestrictionBuilder setEnd(MyTime end) {
        this.end = end;
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

    public Restriction build(){
        return new Restriction(name, limit, start, end, application, group);
    }
}
