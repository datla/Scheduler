package uk.co.scheduler.task.policy;

import org.junit.Test;

import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;
import static uk.co.scheduler.business.domain.MessageBuilder.newMessage;

public class GroupCancellationPolicyTest {

    @Test
    public void isCancelled_true_given_message_belongs_to_cancellation_group(){
        GroupCancellationPolicy policy = new GroupCancellationPolicy();
        policy.register(newGroupId("group-1"));
        assertTrue(policy.isCancelled(newMessage().withGroupId("group-1").build()));
    }

    @Test
    public void isCancelled_false_given_message_does_not_belong_to_cancellation_group(){
        GroupCancellationPolicy policy = new GroupCancellationPolicy();
        policy.register(newGroupId("group-1"));
        assertFalse(policy.isCancelled(newMessage().withGroupId("group-2").build()));
    }
}