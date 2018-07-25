package com.git.hui.task.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by @author YiHui in 18:11 18/6/28.
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumAto = new AtomicInteger(1);
    private final AtomicInteger threadNumAto = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public DefaultThreadFactory(String pool) {
        if (null == pool || "".equals(pool)) {
            pool = "defaultPool";
        }

        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = pool + poolNumAto.getAndIncrement() + "-thread-";
    }


    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r, namePrefix + threadNumAto.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(true);
        }

        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        return thread;
    }
}
