package uk.co.scheduler.task;

import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.callback.TaskExecutionHandler;
import uk.co.scheduler.task.policy.CancellationPolicy;

import java.util.concurrent.Callable;

public class MessageTask implements Callable<Boolean> {

    private final Message message;
    private final Gateway gateway;
    private final ResourceManager resourceManager;
    private TaskExecutionHandler executionHandler;
    private CancellationPolicy cancellationPolicy;

    public MessageTask(Message message,
                       Gateway gateway,
                       ResourceManager resourceManager,
                       TaskExecutionHandler executionHandler,
                       CancellationPolicy cancellationPolicy) {
        this.message = message;
        this.gateway = gateway;
        this.resourceManager = resourceManager;
        this.executionHandler = executionHandler;
        this.cancellationPolicy = cancellationPolicy;
    }

    @Override
    public Boolean call() throws Exception {
        try{
            if(!cancellationPolicy.isCancelled(message)){
                executionHandler.before(message);
                gateway.submit(message);
                gateway.completed();
            }
        }finally {
            resourceManager.release();
        }
        return true;
    }
}
