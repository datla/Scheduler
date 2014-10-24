package uk.co.scheduler.queue;

import uk.co.scheduler.business.datatypes.GroupId;
import uk.co.scheduler.business.domain.GroupIds;
import uk.co.scheduler.business.domain.Message;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroupPriorityMessageQueue implements MessageQueue {
    private final LinkedHashMap<GroupId, Queue<Message>> groupIdMessagesMap = new LinkedHashMap<>();
    private final GroupIds inProcessGroupIds;

    public GroupPriorityMessageQueue(GroupIds groupIds) {
        this.inProcessGroupIds = groupIds;
    }

    @Override
    public void add(Message message) {
        GroupId groupId = message.groupId();
        if (!groupIdMessagesMap.containsKey(groupId)) {
            groupIdMessagesMap.put(groupId, new ConcurrentLinkedQueue<Message>());
        }

        synchronized (groupIdMessagesMap) {
            groupIdMessagesMap.get(groupId).add(message);
        }

    }

    @Override
    public Message get() {
        Message message = findMessageBelongsToGroupsWhichAreUnderProcess();
        if (message == null) {
            message = nextMessageInQueue();
        }

        //updateInProcessGroupIds(message);

        return message;
    }

    @Override
    public boolean isEmpty() {
        return groupIdMessagesMap.isEmpty();
    }

    private Message nextMessageInQueue() {
        Message message = null;
        boolean hasEntry = groupIdMessagesMap.entrySet().iterator().hasNext();
        if (hasEntry) {
            Map.Entry<GroupId, Queue<Message>> entry = groupIdMessagesMap.entrySet().iterator().next();
            message = entry.getValue().poll();
            removeEntryIfNoMoreMessages(entry.getKey());
        }

        return message;
    }

    private Message findMessageBelongsToGroupsWhichAreUnderProcess() {
        Message message = null;
        for (GroupId processingGroupId : inProcessGroupIds) {
            message = findMessage(processingGroupId);
            if(message != null){
                break;
            }
        }
        return message;
    }

    private Message findMessage(GroupId groupId){
        Message message = null;
        if (groupIdMessagesMap.containsKey(groupId)) {
            Queue<Message> messages = groupIdMessagesMap.get(groupId);
            message = messages.poll();
            removeEntryIfNoMoreMessages(groupId);
        }

        return message;
    }

    private void removeEntryIfNoMoreMessages(GroupId groupId) {
        if (groupIdMessagesMap.get(groupId).isEmpty()) {
            synchronized (groupIdMessagesMap) {
                if (groupIdMessagesMap.get(groupId).isEmpty()) {
                    groupIdMessagesMap.remove(groupId);
                }
            }
        }
    }
}
