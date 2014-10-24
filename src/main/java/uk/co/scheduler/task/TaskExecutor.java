package uk.co.scheduler.task;

import uk.co.scheduler.business.domain.Message;

import java.util.concurrent.ExecutorService;

public class TaskExecutor {

    private final ExecutorService executorService;
    private final TaskFactory taskFactory;

    public TaskExecutor(ExecutorService executorService, TaskFactory taskFactory) {
        this.executorService = executorService;
        this.taskFactory = taskFactory;
    }

    public void submit(Message message){
        executorService.submit(taskFactory.createTask(message));
    }
}
