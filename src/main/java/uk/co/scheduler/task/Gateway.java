package uk.co.scheduler.task;

import uk.co.scheduler.business.domain.Message;

public interface Gateway {

    public void submit(Message message);
    public void completed();
}
