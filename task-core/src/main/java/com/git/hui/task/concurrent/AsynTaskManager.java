package com.git.hui.task.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by @author YiHui in 18:11 18/6/28.
 */
@Slf4j
public class AsynTaskManager {

    private static ExecutorService taskExecutors =
            new ThreadPoolExecutor(24, 30, 3, TimeUnit.MINUTES, new LinkedBlockingDeque<>(10),
                    new DefaultThreadFactory("task-execute"), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * @param runnable
     */
    public static void addTask(final Runnable runnable) {
        taskExecutors.submit(runnable);
    }
}
