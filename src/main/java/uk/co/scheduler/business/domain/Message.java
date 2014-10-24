package uk.co.scheduler.business.domain;

import uk.co.scheduler.business.datatypes.GroupId;

public class Message {

    private GroupId groupId;
    private String content;

    public Message(GroupId groupId, String content) {
        this.groupId = groupId;
        this.content = content;
    }

    public GroupId groupId() {
        return groupId;
    }

    public String content() {
        return content;
    }
}
