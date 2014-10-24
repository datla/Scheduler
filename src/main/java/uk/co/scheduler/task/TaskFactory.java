package uk.co.scheduler.task;

import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.callback.TaskExecutionHandler;
import uk.co.scheduler.task.policy.CancellationPolicy;

public class TaskFactory {
    private final Gateway gateway;
    private final ResourceManager resourceManager;
    private final TaskExecutionHandler executionHandler;
    private CancellationPolicy cancellationPolicy;

    public TaskFactory(Gateway gateway,
                       ResourceManager resourceManager,
                       TaskExecutionHandler taskExecutionHandler,
                       CancellationPolicy cancellationPolicy) {
        this.gateway = gateway;
        this.resourceManager = resourceManager;
        this.executionHandler = taskExecutionHandler;
        this.cancellationPolicy = cancellationPolicy;
    }

    public MessageTask createTask(Message message){
        return new MessageTask(message,
                gateway,
                resourceManager,
                executionHandler,cancellationPolicy);
    }

}
