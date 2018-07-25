package com.git.hui.task.util;

import com.git.hui.task.api.ITask;
import com.git.hui.task.exception.CompileTaskScriptException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Created by @author YiHui in 15:44 18/6/28.
 */
@Slf4j
public class ScriptLoadUtil {

    public static ITask loadScript(File file) {
        try {
            return GroovyCompile.compile(file, ITask.class, ScriptLoadUtil.class.getClassLoader());
        } catch (CompileTaskScriptException e) {
            log.error("un-expect error! e: {}", e);
            return null;
        }
    }

}
