package uk.co.scheduler.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ResourceManagerTest {

    @Mock
    private Semaphore mockSemaphore;
    private ResourceManager resourceManager;

    @Before
    public void setUp(){
        resourceManager = new ResourceManager(mockSemaphore);
    }

    @Test
    public void tryAcquireBlocking_should_return_true_given_it_acquires_lock() throws Exception {
        doNothing().when(mockSemaphore).acquire();
        assertTrue(resourceManager.tryAcquireByBlocking());
    }

    @Test
    public void tryAcquireBlocking_should_return_false_given_thread_interruption_exception_thrown() throws Exception {
        doThrow(new InterruptedException()).when(mockSemaphore).acquire();
        assertFalse(resourceManager.tryAcquireByBlocking());
    }

    @Test
    public void tryAcquire_should_acquire_resource() throws Exception {
        when(mockSemaphore.tryAcquire()).thenReturn(true);
        assertTrue(resourceManager.tryAcquire());
    }

    @Test
    public void release_should_release_resource() throws Exception {
        resourceManager.release();
        verify(mockSemaphore).release();
    }
}