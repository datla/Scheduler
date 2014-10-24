package uk.co.scheduler.queue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.TaskExecutor;
import uk.co.scheduler.task.ResourceManager;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.co.scheduler.business.domain.MessageFactory.newDefaultMessage;

@RunWith(MockitoJUnitRunner.class)
public class MessageClientTest {

    @Mock
    private ResourceManager mockResourceManager;
    @Mock
    private MessageQueue mockQueue;
    @Mock
    private TaskExecutor mockTaskExecutor;
    private MessageClient messageClient;
    private Message message = newDefaultMessage();

    @Before
    public void setUp(){
        messageClient = new MessageClient(mockResourceManager, mockQueue, mockTaskExecutor);
    }

    @Test
    public void receive_should_submit_message_to_task_executor_given_resource_available(){
        when(mockResourceManager.tryAcquire()).thenReturn(true);
        messageClient.accept(message);
        verify(mockTaskExecutor).submit(message);
        verifyZeroInteractions(mockQueue);
    }

    @Test
    public void receive_should_add_message_to_queue_given_resource_not_available(){
        when(mockResourceManager.tryAcquire()).thenReturn(false);
        messageClient.accept(message);
        verify(mockQueue).add(message);
        verifyZeroInteractions(mockTaskExecutor);
    }
}