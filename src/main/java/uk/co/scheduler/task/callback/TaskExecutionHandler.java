package uk.co.scheduler.task.callback;

import uk.co.scheduler.business.domain.Message;

public interface TaskExecutionHandler {

    public void before(Message message);
}
