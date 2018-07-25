package com.git.hui.task.watch;

import com.git.hui.task.api.ITask;
import com.git.hui.task.container.TaskContainer;
import com.git.hui.task.task.ScriptTaskDecorate;
import com.git.hui.task.util.ScriptLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author YiHui in 18:20 18/6/28.
 */
@Slf4j
public class TaskChangeWatcher {
    private static final String SCRIPT_TYPE = ".groovy";

    public static boolean registerWatcher(File file) {
        try {
            long period = TimeUnit.SECONDS.toMillis(1);

            // 使用commons-io的文件观察器，实现文件动态变动的监听
            FileAlterationObserver observer =
                    new FileAlterationObserver(file, FileFilterUtils.and(FileFilterUtils.fileFileFilter()));

            observer.addListener(new TaskChangeListener());
            FileAlterationMonitor monitor = new FileAlterationMonitor(period, observer);
            monitor.start();

            return true;
        } catch (Exception e) {
            log.error("register watcher for script task change error! file: {} e:{}", file.getAbsolutePath(), e);
            return false;
        }
    }

    static final class TaskChangeListener extends FileAlterationListenerAdaptor {
        private boolean ignore(File file) {
            return !file.getName().endsWith(SCRIPT_TYPE);
        }

        private void addTask(File file) {
            ITask script = ScriptLoadUtil.loadScript(file);
            if (script == null) {
                return;
            }

            // 更新context中缓存，并启动任务
            ScriptTaskDecorate task = new ScriptTaskDecorate(script);
            TaskContainer.registerTask(file.getAbsolutePath(), task);
        }

        @Override
        public void onFileCreate(File file) {
            if (ignore(file)) {
                return;
            }
            addTask(file);
            // 在线程池中执行task
            log.info("add task : {}", file.getAbsolutePath());
        }

        @Override
        public void onFileChange(File file) {
            if (ignore(file)) {
                return;
            }
            addTask(file);
            // 在线程池中执行task
            log.info("task changed : {}", file.getName());
        }

        @Override
        public void onFileDelete(File file) {
            if (ignore(file)) {
                return;
            }

            // 文件删除，表示需要卸载旧的task
            TaskContainer.removeTask(file.getAbsolutePath());
            log.info("task delete: {}", file.getName());
        }
    }
}
