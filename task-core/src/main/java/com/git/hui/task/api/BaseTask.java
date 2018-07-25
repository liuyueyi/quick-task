package com.git.hui.task.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by @author yihui in 17:53 18/7/20.
 */
public abstract class BaseTask implements ITask {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        before();

        process();

        after();
    }

    /**
     * do something before task run
     */
    protected void before() {
    }

    /**
     * task logic
     */
    public abstract void process();

    /**
     * do something after task run over
     */
    protected void after() {
    }
}
