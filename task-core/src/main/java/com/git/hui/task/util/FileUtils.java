package com.git.hui.task.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by @author YiHui in 19:48 18/6/28.
 */
public class FileUtils {

    public static List<File> loadFiles(String dir, Predicate<File> filter) {
        File file = new File(dir);
        if (file.isFile()) {
            List<File> fileList = new ArrayList<>();
            fileList.add(file);
            return fileList;
        }

        File[] files = file.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> list = new ArrayList<>(files.length);
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(loadFiles(f.getAbsolutePath(), filter));
            } else if (f.isFile() && !filter.test(f)) {
                list.add(f);
            }
        }
        return list;
    }
}
