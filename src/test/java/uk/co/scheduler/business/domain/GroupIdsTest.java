package uk.co.scheduler.business.domain;

import org.junit.Test;
import uk.co.scheduler.business.datatypes.GroupId;

import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;

public class GroupIdsTest {

    @Test
    public void remove_should_delete_only_one_group_id_given_duplicates_exist(){
        GroupIds groupIds = new GroupIds();
        groupIds.add(newGroupId("group-1"));
        groupIds.add(newGroupId("group-1"));

        groupIds.poll();

        Iterator<GroupId> iterator = groupIds.iterator();
        assertThat(iterator.next().value(), is("group-1"));
        assertFalse(iterator.hasNext());
    }
}