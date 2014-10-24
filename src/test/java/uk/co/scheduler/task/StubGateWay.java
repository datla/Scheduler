package uk.co.scheduler.task;

import uk.co.scheduler.business.domain.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class StubGateWay implements Gateway {

    private CountDownLatch latch;
    private Queue<Message> processedMessages = new ConcurrentLinkedQueue<>();

    public StubGateWay(int noOfResources){
        latch = new CountDownLatch(noOfResources);
    }

    @Override
    public void submit(Message message) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            //ignore exception
        }

        processedMessages.add(message);
    }

    @Override
    public void completed() {
    }

    public CountDownLatch getLatch(){
        return latch;
    }

    public Queue<Message> getProcessedMessages() {
        return processedMessages;
    }
}