package com.git.hui.task

import com.git.hui.task.api.ITask

/**
 * Created by @author YiHui in 20:28 18/6/28.
 */
class DemoScript implements ITask {
    @Override
    void run() {
        println name() + " | now > : >>" + System.currentTimeMillis()
    }

    @Override
    void interrupt() {
        println "原来的结束 over"
    }
}
