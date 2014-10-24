package uk.co.scheduler.task;

import java.util.concurrent.Semaphore;

public class ResourceManager {
    private final Semaphore semaphore;

    public ResourceManager(int noOfResources) {
        this.semaphore = new Semaphore(noOfResources, true);
    }

    ResourceManager(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public boolean tryAcquireByBlocking()  {
        try {
            semaphore.acquire();
            return true;
        } catch (InterruptedException e) {
           return false;
        }
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    public void release() {
        semaphore.release();
    }
}
