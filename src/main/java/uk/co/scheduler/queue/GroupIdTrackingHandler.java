package uk.co.scheduler.queue;

import uk.co.scheduler.business.domain.GroupIds;
import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.callback.TaskExecutionHandler;

//This can even be tracked by ThreadPoolExecutor beforeExecute() call back methods
public class GroupIdTrackingHandler implements TaskExecutionHandler {

    private final GroupIds groupIds;

    public GroupIdTrackingHandler(GroupIds groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public void before(Message message) {
        groupIds.poll();
        groupIds.add(message.groupId());
    }
}
