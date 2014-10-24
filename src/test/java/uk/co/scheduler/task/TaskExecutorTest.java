package uk.co.scheduler.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.scheduler.business.domain.Message;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.scheduler.business.domain.MessageFactory.newDefaultMessage;

@RunWith(MockitoJUnitRunner.class)
public class TaskExecutorTest {

    @Mock
    private ExecutorService mockExecutorService;
    @Mock
    private TaskFactory mockTaskFactory;
    private TaskExecutor taskExecutor;
    @Mock
    private MessageTask task;

    @Before
    public void setUp(){
        taskExecutor = new TaskExecutor(mockExecutorService, mockTaskFactory);
    }

    @Test
    public void submit_should_create_task_and_submit_to_executor_service(){
        Message message = newDefaultMessage();
        when(mockTaskFactory.createTask(message)).thenReturn(task);
        taskExecutor.submit(message);
        verify(mockExecutorService).submit(task);
    }
}