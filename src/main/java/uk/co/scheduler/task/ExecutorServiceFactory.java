package uk.co.scheduler.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceFactory {

    public static ExecutorService newExecutorService(int noOfThreads){
        return  new ThreadPoolExecutor(0, noOfThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
}
