package com.git.hui.task.impl;

import com.git.hui.task.api.BaseTask;

/**
 * Created by @author yihui in 21:25 18/7/19.
 */
public abstract class BaseScheduleTask extends BaseTask {

    protected volatile boolean run = false;

    private long sleepTime;

    @Override
    public void process() {
        run = true;
        sleepTime = getScheduleTime();
        while (true) {
            if (!run) {
                break;
            }

            doProcess();

            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                log.error("task: {}, exception: {}", getClass(), e);
            }
        }
    }

    /**
     * 初始化间隔时间
     *
     * @return
     */
    protected abstract long getScheduleTime();

    /**
     * 具体的任务调度逻辑
     */
    protected abstract void doProcess();

    @Override
    public void interrupt() {
        run = false;
    }
}
