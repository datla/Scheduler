package uk.co.scheduler.business.domain;


import uk.co.scheduler.business.datatypes.GroupId;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroupIds implements  Iterable<GroupId> {
    private final Queue<GroupId> ids = new ConcurrentLinkedQueue<>();

    public void add(GroupId id){
        ids.add(id);
    }

    public GroupId poll(){
        return ids.poll();
    }

    @Override
    public Iterator<GroupId> iterator() {
        return ids.iterator();
    }
}
