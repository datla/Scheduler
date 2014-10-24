package uk.co.scheduler.task.policy;

import uk.co.scheduler.business.datatypes.GroupId;
import uk.co.scheduler.business.domain.Message;

public interface CancellationPolicy {

    public boolean isCancelled(Message message);

}
