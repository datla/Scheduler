package uk.co.scheduler.business.domain;

import uk.co.scheduler.business.datatypes.GroupId;

import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;
import static uk.co.scheduler.business.domain.MessageBuilder.newMessage;

public class MessageFactory {

    public static Message newDefaultMessage(){
        return newMessage().withGroupId("defaultGroupId")
                .withContent("defaultContent").build();
    }
}