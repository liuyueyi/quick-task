package com.git.hui.base;

import groovy.lang.GroovyClassLoader;

/**
 * Created by @author yihui in 17:21 18/7/23.
 */
public class LoaderTest {
    static GroovyClassLoader loader = new GroovyClassLoader(LoaderTest.class.getClassLoader());
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        String content = "package com.git.hui.base;\n" + "\n" + "import java.util.Objects;\n" + "\n" +
                "public class LoaderClz {\n" + "    public static String name=\"123\";\n" + "\n" + "    @Override\n" +
                "    public boolean equals(Object o) {\n" + "        if (this == o) {\n" +
                "            return true;\n" + "        }\n" +
                "        if (o == null || getClass() != o.getClass()) {\n" + "            return false;\n" +
                "        }\n" + "        LoaderClz loaderClz = (LoaderClz) o;\n" +
                "        return Objects.equals(name, loaderClz.name);\n" + "    }\n" + "}\n";

        Class clz = loader.parseClass(content);
        Object loaderClz = clz.newInstance();
        System.out.println(loaderClz);

        content =  "package com.git.hui.base;\n" + "\n" + "import java.util.Objects;\n" + "\n" +
                "public class LoaderClz {\n" + "    public static String name=\"123\"; public static int num=10;\n" +
                "\n" + "    @Override\n" +
                "    public boolean equals(Object o) {\n" + "        if (this == o) {\n" +
                "            return true;\n" + "        }\n" +
                "        if (o == null || getClass() != o.getClass()) {\n" + "            return false;\n" +
                "        }\n" + "        LoaderClz loaderClz = (LoaderClz) o;\n" +
                "        return Objects.equals(name, loaderClz.name);\n" + "    }\n" + "}\n";
        clz = loader.parseClass(content);
        loaderClz = clz.newInstance();
        System.out.println(loaderClz);

        loader.clearCache();
        content =  "package com.git.hui.base;\n" + "\n" + "import java.util.Objects;\n" + "\n" +
                "public class LoaderClz {\n" + "    public final static String name=\"456\";\n" + "\n" + "    " +
                "@Override\n" +
                "    public boolean equals(Object o) {\n" + "        if (this == o) {\n" +
                "            return true;\n" + "        }\n" +
                "        if (o == null || getClass() != o.getClass()) {\n" + "            return false;\n" +
                "        }\n" + "        LoaderClz loaderClz = (LoaderClz) o;\n" +
                "        return Objects.equals(name, loaderClz.name);\n" + "    }\n" + "}\n";
        clz = loader.parseClass(content);
        loaderClz = clz.newInstance();
        System.out.println(loaderClz);
    }

}
