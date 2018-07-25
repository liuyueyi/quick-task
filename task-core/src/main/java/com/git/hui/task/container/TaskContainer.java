package com.git.hui.task.container;

import com.git.hui.task.concurrent.AsynTaskManager;
import com.git.hui.task.task.ScriptTaskDecorate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by @author YiHui in 18:11 18/6/28.
 */
public class TaskContainer {
    /**
     * key: com.git.hui.task.api.ITask#name()
     */
    private static Map<String, ScriptTaskDecorate> taskCache = new ConcurrentHashMap<>();

    /**
     * key: absolute script path
     *
     * for task to delete
     */
    private static Map<String, ScriptTaskDecorate> pathCache = new ConcurrentHashMap<>();

    public static void registerTask(String path, ScriptTaskDecorate task) {
        ScriptTaskDecorate origin = taskCache.get(task.getName());
        if (origin != null) {
            origin.interrupt();
        }
        taskCache.put(task.getName(), task);
        pathCache.put(path, task);
        AsynTaskManager.addTask(task);
    }

    public static void removeTask(String path) {
        ScriptTaskDecorate task = pathCache.get(path);
        if (task != null) {
            task.interrupt();
            taskCache.remove(task.getName());
            pathCache.remove(path);
        }
    }
}
