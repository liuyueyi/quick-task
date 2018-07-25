package com.git.hui.task

import com.git.hui.task.api.ITask

/**
 * Created by @author YiHui in 14:56 18/7/2.
 */
class PrintScript implements ITask {
    @Override
    void run() {
        println name() + " | print script run"
    }

    @Override
    void interrupt() {
        println "print script over!"
    }

    public static void main(String[] args) {
        PrintScript printScript = PrintScript.class.newInstance();
        printScript.run()
    }
}
