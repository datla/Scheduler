package uk.co.scheduler.queue;

import org.junit.Test;
import uk.co.scheduler.business.domain.GroupIds;
import uk.co.scheduler.business.domain.Message;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;
import static uk.co.scheduler.business.domain.MessageBuilder.newMessage;

public class GroupIdTrackingHandlerTest {

    private GroupIds groupIds = new GroupIds();
    private Message message = newMessage().withGroupId("group-2").build();

    @Test
    public void doBefore_should_delete_previous_message_and_add_new_message(){
        groupIds.add(newGroupId("group-1"));
        GroupIdTrackingHandler handler = new GroupIdTrackingHandler(groupIds);

        handler.before(message);

        assertThat(groupIds.poll(), is(message.groupId()));
        assertNull(groupIds.poll());
    }
}