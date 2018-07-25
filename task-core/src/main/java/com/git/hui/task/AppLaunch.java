package com.git.hui.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * Created by @author YiHui in 15:31 18/7/2.
 */
@Slf4j
public class AppLaunch {
    private static final String SOURCE_PATH = "./task-core/src/test/java/com/git/hui/task";
    private static final String TASK_ARG_LONG = "task";
    private static final String TASK_ARG_SHORT = "t";
    private static final String ARG_HELP_LONG = "help";
    private static final String ARG_HELP_SHORT = "h";
    private static volatile boolean run = true;


    private static void printHelp() {
        Options options = buildOptions();
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar ${jar} [options]", options);
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(
                Option.builder(TASK_ARG_SHORT).argName(TASK_ARG_LONG).hasArg().longOpt(TASK_ARG_LONG).required(false)
                        .desc("choose task path, default [" + SOURCE_PATH + "]").build());
        options.addOption(Option.builder(ARG_HELP_SHORT).longOpt(ARG_HELP_LONG).desc("show command help").build());
        return options;
    }

    private static CommandLine parseArguments(String[] arguments) {
        Options options = buildOptions();
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, arguments);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (commandLine.hasOption(ARG_HELP_LONG)) {
            printHelp();
            System.exit(0);
        }
        return commandLine;
    }


    public static void main(String[] args) throws InterruptedException {
        CommandLine commandLine = parseArguments(args);
        String scriptSource = commandLine.getOptionValue(TASK_ARG_LONG, SOURCE_PATH);
        System.out.println("script source: {}" + scriptSource);
        new ScriptExecuteEngine().run(scriptSource);
        registerHook();
        while (run) {
            Thread.sleep(1000 * 10 * 10);
        }
        log.info("application over!!!!");
    }


    /**
     * 注册一个程序关闭的钩子, 用于回收现场
     */
    private static void registerHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread") {
            @Override
            public void run() {
                log.info("closing Application......");
                run = false;
                log.info("closing over........");
            }
        });
    }
}
