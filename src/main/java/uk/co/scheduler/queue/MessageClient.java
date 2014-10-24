package uk.co.scheduler.queue;

import uk.co.scheduler.task.TaskExecutor;
import uk.co.scheduler.task.ResourceManager;
import uk.co.scheduler.business.domain.Message;

public class MessageClient {

    private final ResourceManager resourceManager;
    private final MessageQueue messageQueue;
    private final TaskExecutor taskExecutor;

    public MessageClient(ResourceManager resourceManager,
                         MessageQueue messageQueue,
                         TaskExecutor taskExecutor) {
        this.resourceManager = resourceManager;
        this.messageQueue = messageQueue;
        this.taskExecutor = taskExecutor;
    }

    public void accept(Message message){
        if(resourceManager.tryAcquire()){
            taskExecutor.submit(message);
        }else{
            messageQueue.add(message);
        }
    }
}
