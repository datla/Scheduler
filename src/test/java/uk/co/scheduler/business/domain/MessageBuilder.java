package uk.co.scheduler.business.domain;

import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;

public class MessageBuilder {

    private String groupId;
    private String content = "";

    private MessageBuilder(){

    }

    public static MessageBuilder newMessage(){
        return new MessageBuilder();
    }

    public Message build(){
        if(groupId == null){
            throw new IllegalArgumentException("group id can't be null");
        }
        return new Message(newGroupId(groupId), content);
    }

    public MessageBuilder withGroupId(String id){
        this.groupId = id;
        return this;
    }

    public MessageBuilder withContent(String content){
        this.content = content;
        return this;
    }
}
