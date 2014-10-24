package uk.co.scheduler.business.datatypes;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;

public class GroupIdTest {

    @Test
    public void equals_true(){
        assertTrue(newGroupId("group-1").equals(newGroupId("group-1")));
    }

    @Test
    public void equals_false(){
        assertFalse(newGroupId("group-1").equals(newGroupId("group-2")));
    }

    @Test
    public void hash_code_should_be_derived_from_id_value(){
        assertThat(newGroupId("group-1").hashCode(), is("group-1".hashCode()));
    }

    @Test
    public void hash_code_should_be_same_for_equal_objects(){
        GroupId instance1 = newGroupId("group-1");
        GroupId instance2 = newGroupId("group-1");
        assertTrue(instance1.equals(instance2));
        assertTrue(instance1.hashCode() == instance2.hashCode());
    }
}