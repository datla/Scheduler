package uk.co.scheduler.task.policy;

import uk.co.scheduler.business.datatypes.GroupId;
import uk.co.scheduler.business.domain.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupCancellationPolicy implements CancellationPolicy {

    private final List<GroupId> groupIds;

    public GroupCancellationPolicy() {
        this.groupIds = new CopyOnWriteArrayList<>();
    }

    public void register(GroupId id){
        groupIds.add(id);
    }

    @Override
    public boolean isCancelled(Message message) {
        return groupIds.contains(message.groupId());
    }
}
