package uk.co.scheduler.queue;

import uk.co.scheduler.business.domain.Message;

import java.util.Comparator;
import java.util.PriorityQueue;

public interface MessageQueue {

    public void add(Message message);

    public Message get();

    public boolean isEmpty();
}
