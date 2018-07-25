package com.git.hui.task.task;

import com.git.hui.task.api.ITask;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by @author YiHui in 18:12 18/6/28.
 */
@Slf4j
public class ScriptTaskDecorate extends Thread {
    private ITask task;

    public ScriptTaskDecorate(ITask task) {
        this.task = task;
        setName(task.name());
    }

    @Override
    public void run() {
        try {
            task.run();
        } catch (Exception e) {
            log.error("script task run error! task: {}", task.name());
        }
    }

    @Override
    public void interrupt() {
        task.interrupt();
    }
}
