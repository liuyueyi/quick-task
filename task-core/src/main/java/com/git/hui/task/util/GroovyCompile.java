package com.git.hui.task.util;

import com.git.hui.task.exception.CompileTaskScriptException;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Created by @author YiHui in 15:23 18/6/28.
 */
@Slf4j
public class GroovyCompile {

    @SuppressWarnings("unchecked")
    public static <T> T compile(File codeSource, Class<T> interfaceType, ClassLoader classLoader)
            throws CompileTaskScriptException {
        try {
            GroovyClassLoader loader = new GroovyClassLoader(classLoader);
            Class clz = loader.parseClass(codeSource);

            // 接口校验
            if (!interfaceType.isAssignableFrom(clz)) {
                throw new CompileTaskScriptException("illegal script type!");
            }

            return (T) clz.newInstance();
        } catch (IOException e) {
            log.error("load code from {} error! e: {}", codeSource, e);
            throw new CompileTaskScriptException("load code from " + codeSource + " error!");
        } catch (CompileTaskScriptException e) {
            throw e;
        } catch (Exception e) {
            log.error("initial script error! codePath: {}, e: {}", codeSource, e);
            throw new CompileTaskScriptException(
                    "initial script error! clz: " + codeSource + " msg: " + e.getMessage());
        }
    }
}
