package uk.co.scheduler.queue;

import org.junit.Before;
import org.junit.Test;
import uk.co.scheduler.business.domain.GroupIds;
import uk.co.scheduler.business.domain.Message;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;

public class GroupPriorityMessageQueueTest {

    private MessageQueue queue;
    private GroupIds groupIdsUnderProcess;

    @Before
    public void setUp(){
        groupIdsUnderProcess = new GroupIds();
        queue = new GroupPriorityMessageQueue(groupIdsUnderProcess);
    }

    @Test
    public void get_should_give_priority_to_message_that_belongs_to_groups_which_are_under_process() {
        groupIdsUnderProcess.add(newGroupId("group-1"));
        queue.add(newMessage("group-1", "message-1"));
        queue.add(newMessage("group-2", "message-2"));
        queue.add(newMessage("group-1", "message-3"));

        assertThat(queue.get().content(), is("message-1"));
        assertThat(queue.get().content(), is("message-3"));
        assertThat(queue.get().content(), is("message-2"));
        assertNull(queue.get());
    }

    @Test
    public void get_should_return_messages_in_the_order_of_insertion_given_no_messages_belong_to_groups_which_are_under_process() {
        groupIdsUnderProcess.add(newGroupId("group-1"));
        queue.add(newMessage("group-2", "message-1"));
        queue.add(newMessage("group-3", "message-2"));
        queue.add(newMessage("group-4", "message-3"));

        assertThat(queue.get().content(), is("message-1"));
        assertThat(queue.get().content(), is("message-2"));
        assertThat(queue.get().content(), is("message-3"));
        assertNull(queue.get());
    }

    @Test
    public void get_should_return_messages_in_the_order_of_insertion_given_no_groups_under_process() {
        queue.add(newMessage("group-2", "message-1"));
        queue.add(newMessage("group-2", "message-2"));
        queue.add(newMessage("group-3", "message-3"));
        queue.add(newMessage("group-4", "message-4"));

        assertThat(queue.get().content(), is("message-1"));
        assertThat(queue.get().content(), is("message-2"));
        assertThat(queue.get().content(), is("message-3"));
        assertThat(queue.get().content(), is("message-4"));
        assertNull(queue.get());
    }

    @Test
    public void get_should_give_priority_to_messages_that_belongs_to_groups_that_are_already_added() {
        groupIdsUnderProcess.add(newGroupId("group-1"));
        queue.add(newMessage("group-1", "message-1"));
        queue.add(newMessage("group-2", "message-2"));
        queue.add(newMessage("group-1", "message-3"));

        assertThat(queue.get().content(), is("message-1"));
        groupIdsUnderProcess.add(newGroupId("group-2"));

        assertThat(queue.get().content(), is("message-3"));
        assertThat(queue.get().content(), is("message-2"));
        assertNull(queue.get());
    }

    @Test
    public void get_should_not_give_priority_to_messages_that_belongs_to_group_which_has_been_deleted_from_under_process_list_and_give_priority_when_it_is_added_again() {
        groupIdsUnderProcess.add(newGroupId("group-1"));
        queue.add(newMessage("group-1", "message-1"));
        queue.add(newMessage("group-2", "message-2"));
        queue.add(newMessage("group-2", "message-3"));

        assertThat(queue.get().content(), is("message-1"));

        groupIdsUnderProcess.poll();
        queue.add(newMessage("group-1", "message-4"));
        queue.add(newMessage("group-1", "message-5"));

        assertThat(queue.get().content(), is("message-2"));
        assertThat(queue.get().content(), is("message-3"));

        groupIdsUnderProcess.add(newGroupId("group-1"));
        assertThat(queue.get().content(), is("message-4"));
        assertThat(queue.get().content(), is("message-5"));

        assertNull(queue.get());
    }

    @Test
    public void get_should_give_priority_to_next_message_in_the_queue_given_no_more_messages_exist_to_the_group_under_process() {
        groupIdsUnderProcess.add(newGroupId("group-1"));
        queue.add(newMessage("group-1", "message-1"));
        queue.add(newMessage("group-2", "message-2"));
        queue.add(newMessage("group-1", "message-3"));

        assertThat(queue.get().content(), is("message-1"));
        assertThat(queue.get().content(), is("message-3"));
        assertThat(queue.get().content(), is("message-2"));
        assertNull(queue.get());
    }

    @Test
    public void isEmpty_true() {
        assertTrue(queue.isEmpty());
    }

    @Test
    public void isEmpty_false() {
        queue.add(newMessage("group-1", "message-1"));
        assertFalse(queue.isEmpty());
    }

    private Message newMessage(String groupId, String content) {
        return new Message(newGroupId(groupId), content);
    }
}