package uk.co.scheduler.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.scheduler.business.domain.Message;
import uk.co.scheduler.task.callback.TaskExecutionHandler;
import uk.co.scheduler.task.policy.CancellationPolicy;

import static org.mockito.Mockito.*;
import static uk.co.scheduler.business.domain.MessageFactory.newDefaultMessage;

@RunWith(MockitoJUnitRunner.class)
public class MessageTaskTest {

    @Mock
    private Gateway mockGateway;
    @Mock
    private ResourceManager mockResourceManager;
    @Mock
    private TaskExecutionHandler mockExecutionHandler;
    @Mock
    private CancellationPolicy mockCancellationPolicy;
    private Message message = newDefaultMessage();
    private MessageTask task;

    @Before
    public void setUp(){
        task = new MessageTask(message, mockGateway, mockResourceManager, mockExecutionHandler, mockCancellationPolicy);
    }

    @Test
    public void call_should_submit_message_to_gateWay() throws Exception {
        task.call();
        verify(mockGateway).submit(message);
    }

    @Test
    public void call_should_invoke_completed_on_gateWay() throws Exception {
        task.call();
        verify(mockGateway).completed();
    }

    @Test
    public void call_should_release_resource() throws Exception {
        task.call();
        verify(mockResourceManager).release();
    }

    @Test
    public void call_should_release_resource_given_exception_thrown() {
        try{
            doThrow(new RuntimeException()).when(mockGateway).submit(message);
            task.call();
        }catch (Exception e){
            //ignore exception
        }
        verify(mockResourceManager).release();
    }

    @Test
    public void call_should_execute_handler() throws Exception {
        task.call();
        verify(mockExecutionHandler).before(message);
    }

    @Test
    public void call_should_ignore_message_given_cancelled() throws Exception {
        when(mockCancellationPolicy.isCancelled(message)).thenReturn(true);
        task.call();
        verify(mockResourceManager).release();
        verifyZeroInteractions(mockGateway);
        verifyZeroInteractions(mockExecutionHandler);
    }

    @Test
    public void call_should_perform_actions_in_order() throws Exception {
        task.call();
        InOrder inOrder = inOrder(mockGateway, mockResourceManager);
        inOrder.verify(mockGateway).submit(message);
        inOrder.verify(mockGateway).completed();
        inOrder.verify(mockResourceManager).release();
    }
}