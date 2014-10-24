package uk.co.scheduler.integration;

import uk.co.scheduler.business.domain.GroupIds;
import uk.co.scheduler.queue.*;
import uk.co.scheduler.task.*;
import uk.co.scheduler.task.policy.CancellationPolicy;
import uk.co.scheduler.task.policy.GroupCancellationPolicy;

import static uk.co.scheduler.queue.MessageQueueConsumer.PollingCondition;
import static uk.co.scheduler.task.ExecutorServiceFactory.newExecutorService;

public class ApplicationContext {

    private MessageClient messageClient;//required to send messages from test
    private MessageQueue messageQueue; //required to validate messages size
    private StubGateWay gateway; //required to validate message ordering
    private CancellationPolicy cancellationPolicy; //required to test group cancellation
    private MessageQueueConsumer queueConsumer; //required to start message processing thread from test
    private ResourceManager resourceManager;

    public void init(int noOfResources){
        resourceManager = new ResourceManager(noOfResources);
        GroupIds groupIds = new GroupIds();
        gateway = new StubGateWay(noOfResources);
        GroupIdTrackingHandler groupIdTrackingHandler = new GroupIdTrackingHandler(groupIds);
        cancellationPolicy = new GroupCancellationPolicy();

        TaskFactory taskFactory = new TaskFactory(gateway,
                resourceManager,
                groupIdTrackingHandler, cancellationPolicy);

        TaskExecutor taskExecutor = new TaskExecutor(newExecutorService(noOfResources),
                taskFactory);

        messageQueue = new GroupPriorityMessageQueue(groupIds);
        messageClient = new MessageClient(resourceManager, messageQueue, taskExecutor);
        queueConsumer = new MessageQueueConsumer(resourceManager, messageQueue, taskExecutor, new PollingCondition());
    }

    public MessageClient getMessageClient() {
        return messageClient;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public StubGateWay getStubGateWay() {
        return gateway;
    }

    public MessageQueueConsumer getQueueConsumer() {
        return queueConsumer;
    }

    public CancellationPolicy getCancellationPolicy() {
        return cancellationPolicy;
    }

    public ResourceManager getResourceManager(){
        return resourceManager;
    }
}
