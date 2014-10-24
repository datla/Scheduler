package uk.co.scheduler.queue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.TaskExecutor;
import uk.co.scheduler.task.ResourceManager;

import static org.mockito.Mockito.*;
import static uk.co.scheduler.business.domain.MessageFactory.newDefaultMessage;

@RunWith(MockitoJUnitRunner.class)
public class MessageQueueConsumerTest {

    @Mock
    private ResourceManager mockResourceManager;
    @Mock
    private MessageQueue mockQueue;
    @Mock
    private TaskExecutor mockTaskExecutor;
    @Mock
    private MessageQueueConsumer.PollingCondition mockPollingCondition;
    private MessageQueueConsumer messageQueueConsumer;
    private Message message = newDefaultMessage();

    @Before
    public void setUp(){
        messageQueueConsumer = new MessageQueueConsumer(mockResourceManager, mockQueue, mockTaskExecutor, mockPollingCondition);
    }

    @Test
    public void process_should_submit_message_given_resource_available_and_message_not_empty(){
        when(mockResourceManager.tryAcquireByBlocking()).thenReturn(true);
        when(mockQueue.get()).thenReturn(message);
        when(mockPollingCondition.isSatisfied()).thenReturn(true).thenReturn(false);

        messageQueueConsumer.process();

        verify(mockTaskExecutor).submit(message);
    }

    @Test
    public void process_should_not_submit_message_given_resource_available_and_message_empty(){
        when(mockResourceManager.tryAcquireByBlocking()).thenReturn(true);
        when(mockQueue.get()).thenReturn(null);
        when(mockPollingCondition.isSatisfied()).thenReturn(true).thenReturn(false);

        messageQueueConsumer.process();

        verifyZeroInteractions(mockTaskExecutor);
        verify(mockResourceManager).release();
    }

    @Test
    public void process_should_continue_looping_given_resource_not_available(){
        when(mockResourceManager.tryAcquireByBlocking()).thenReturn(false);
        when(mockPollingCondition.isSatisfied()).thenReturn(true).thenReturn(false);

        messageQueueConsumer.process();

        verifyZeroInteractions(mockTaskExecutor);
        verifyZeroInteractions(mockQueue);
        verify(mockResourceManager).tryAcquireByBlocking();
        verifyNoMoreInteractions(mockResourceManager);
    }
}