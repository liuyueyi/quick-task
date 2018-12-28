package com.git.hui.task.api;

/**
 * Created by @author YiHui in 18:10 18/6/28.
 */
public interface ITask {
    /**
     * 默认将task的类名作为唯一标识
     *
     * @return
     */
    default String name() {
        return this.getClass().getName();
    }

    /**
     * 开始执行任务
     */
    void run();

    /**
     * 任务中断
     */
    default void interrupt() {}
}
