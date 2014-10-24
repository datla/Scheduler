package uk.co.scheduler.queue;

import uk.co.scheduler.task.TaskExecutor;
import uk.co.scheduler.task.ResourceManager;
import uk.co.scheduler.business.domain.Message;

public class MessageQueueConsumer {
    private final ResourceManager resourceManager;
    private final MessageQueue messageQueue;
    private final TaskExecutor taskExecutor;
    private PollingCondition pollingCondition;

    public MessageQueueConsumer(ResourceManager resourceManager,
                                MessageQueue messageQueue,
                                TaskExecutor taskExecutor,
                                PollingCondition pollingCondition) {
        this.resourceManager = resourceManager;
        this.messageQueue = messageQueue;
        this.taskExecutor = taskExecutor;
        this.pollingCondition = pollingCondition;
    }

    public void process() {
        while (pollingCondition.isSatisfied()) {
            boolean resourceAvailable = resourceManager.tryAcquireByBlocking();
            if (resourceAvailable) {
                Message message = messageQueue.get();
                if (message != null) {
                    taskExecutor.submit(message);
                } else {
                    resourceManager.release();
                }
            }
        }
    }

    public static class PollingCondition {
        public boolean isSatisfied(){
            return !Thread.currentThread().isInterrupted();
        }
    }
}
