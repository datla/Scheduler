package uk.co.scheduler.integration;

import org.junit.Before;
import org.junit.Test;
import uk.co.scheduler.business.datatypes.GroupId;
import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.queue.MessageClient;
import uk.co.scheduler.queue.MessageQueue;
import uk.co.scheduler.task.ResourceManager;
import uk.co.scheduler.task.StubGateWay;
import uk.co.scheduler.task.policy.GroupCancellationPolicy;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static uk.co.scheduler.business.datatypes.GroupId.newGroupId;
import static uk.co.scheduler.business.domain.MessageBuilder.newMessage;

public class SchedulerIntegrationTest {

    private ApplicationContext applicationContext;
    private CountDownLatch queueConsumerLatch;
    private int noOfResources = 2;
    private StubGateWay gateWay;
    private ResourceManager resourceManager;
    private MessageQueue messageQueue;

    @Before
    public void setUp(){
        applicationContext = new ApplicationContext();
        applicationContext.init(noOfResources);
        gateWay = applicationContext.getStubGateWay();
        resourceManager = applicationContext.getResourceManager();
        messageQueue = applicationContext.getMessageQueue();

        queueConsumerLatch = new CountDownLatch(1);
    }

    @Test(timeout = 1000 * 5)
    public void messages_belonging_to_in_process_groups_should_be_given_priority() throws Exception {
        MessageClient client = applicationContext.getMessageClient();
        client.accept(newMessage()
                .withGroupId("group-1")
                .withContent("message-1:group-1").build());
        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-2:group-2").build());
        client.accept(newMessage()
                .withGroupId("group-3")
                .withContent("message-3:group-3").build());
        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-4:group-2").build());
        client.accept(newMessage()
                .withGroupId("group-4")
                .withContent("message-5:group-4").build());
        client.accept(newMessage()
                .withGroupId("group-3")
                .withContent("message-6:group-3").build());


        startMessageProcessing();
        waitUntilAllMessagesAreProcessed(6);

        Queue<Message> processedMessages = gateWay.getProcessedMessages();
        assertThat(processedMessages.poll().content(), is("message-1:group-1"));
        assertThat(processedMessages.poll().content(), is("message-2:group-2"));
        assertThat(processedMessages.poll().content(), is("message-4:group-2"));
        assertThat(processedMessages.poll().content(), is("message-3:group-3"));
        assertThat(processedMessages.poll().content(), is("message-6:group-3"));
        assertThat(processedMessages.poll().content(), is("message-5:group-4"));
    }

    @Test
    public void messages_should_be_added_to_queue_and_not_submitted_to_gateway_given_resources_not_available() throws Exception {
        MessageClient client = applicationContext.getMessageClient();

        //acquire resources
        assertTrue(resourceManager.tryAcquire());
        assertTrue(resourceManager.tryAcquire());

        startGateWayProcessing();

        assertTrue(messageQueue.isEmpty());

        client.accept(newMessage()
                .withGroupId("group-1")
                .withContent("message-1:group-1").build());

        assertFalse(messageQueue.isEmpty());
        assertNull(gateWay.getProcessedMessages().poll());
    }

    @Test(timeout = 1000 * 5)
    public void messages_should_be_submitted_directly_and_not_to_be_added_to_queue_given_resources_available() throws Exception {
        MessageClient client = applicationContext.getMessageClient();

        //block queue processing
        startQueueConsumerThread(new CountDownLatch(1));
        startGateWayProcessing();

       client.accept(newMessage()
                .withGroupId("group-1")
                .withContent("message-1:group-1").build());
        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-2:group-2").build());

        assertTrue(messageQueue.isEmpty());

        waitUntilAllMessagesAreProcessed(2);
        Queue<Message> processedMessages = gateWay.getProcessedMessages();
        assertThat(processedMessages.poll().content(), is("message-1:group-1"));
        assertThat(processedMessages.poll().content(), is("message-2:group-2"));
    }

    @Test(timeout = 1000 * 5)
    public void message_should_not_be_submitted_given_group_cancelled() throws Exception {
        MessageClient client = applicationContext.getMessageClient();

        cancelGroup(newGroupId("group-2"));

        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-1:group-2").build());
        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-2:group-2").build());
        client.accept(newMessage()
                .withGroupId("group-1")
                .withContent("message-3:group-1").build());
        client.accept(newMessage()
                .withGroupId("group-2")
                .withContent("message-4:group-2").build());

        startMessageProcessing();

        Thread.sleep(1000*2);
        //waitUntilAllMessagesAreProcessed(1);
        Queue<Message> processedMessages = gateWay.getProcessedMessages();
        assertThat(processedMessages.poll().content(), is("message-3:group-1"));
        assertNull(processedMessages.poll());
    }

    private void startMessageProcessing() {
        startGateWayProcessing();
        startQueueConsumerThread(queueConsumerLatch);
        startQueueProcessing();
    }

    private void cancelGroup(GroupId groupId) {
        ((GroupCancellationPolicy)applicationContext.getCancellationPolicy()).register(groupId);
    }

    private void waitUntilAllMessagesAreProcessed(int noOfMessages) throws InterruptedException {
        while(gateWay.getProcessedMessages().size() != noOfMessages){
            //Just to free up CPU utilisation and to avoid continuous polling
            Thread.sleep(100);
        }
    }

    private void startQueueConsumerThread(final CountDownLatch latch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    //ignore exception
                }

                applicationContext.getQueueConsumer().process();
            }
        }).start();
    }

    private void startGateWayProcessing() {
        CountDownLatch gateWayLatch = gateWay.getLatch();
        for(int i=0;i<noOfResources;i++){
            gateWayLatch.countDown();
        }
    }

    private void startQueueProcessing() {
        queueConsumerLatch.countDown();
        queueConsumerLatch.countDown();
    }
}
