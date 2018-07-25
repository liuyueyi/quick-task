package com.git.hui.task;

import com.git.hui.task.api.ITask;
import com.git.hui.task.container.TaskContainer;
import com.git.hui.task.task.ScriptTaskDecorate;
import com.git.hui.task.util.FileUtils;
import com.git.hui.task.util.ScriptLoadUtil;
import com.git.hui.task.watch.TaskChangeWatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by @author YiHui in 18:08 18/6/28.
 */
@Slf4j
public class ScriptExecuteEngine {
    private static final String SCRIPT_TYPE = ".groovy";

    public void run(String source) {
        List<File> scripts = FileUtils.loadFiles(source, new Predicate<File>() {
            @Override
            public boolean test(File file) {
                return !file.getName().endsWith(SCRIPT_TYPE);
            }
        });

        ITask task;
        ScriptTaskDecorate scriptTask;
        for (File f : scripts) {
            task = ScriptLoadUtil.loadScript(f);
            if (task == null) {
                continue;
            }

            scriptTask = new ScriptTaskDecorate(task);
            TaskContainer.registerTask(f.getAbsolutePath(), scriptTask);
        }

        try {
            TaskChangeWatcher.registerWatcher(new File(source));
        } catch (Exception e) {
            log.error("register task change watcher error! e:{}", e);
            System.exit(1);
        }
    }
}
