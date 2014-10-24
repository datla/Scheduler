package uk.co.scheduler.business.datatypes;

public class GroupId {

    private final String id;

    private GroupId(String id) {
        this.id = id;
    }

    public static GroupId newGroupId(String id){
        if(id == null){
            throw new IllegalArgumentException("id can't be null");
        }
        return new GroupId(id);
    }

    public String value() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupId groupId = (GroupId) o;

        return groupId.value().equals(this.value());
    }
}
