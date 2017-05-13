package com.dubboclub.dk.collector;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Zetas on 2016/7/11.
 */
public class CollectorManager {

    private static volatile boolean running = false;

    private static final String STOP_COMMAND = "stop";

    private static final String START_COMMAND = "start";

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("args must not be empty");
        }
        if (STOP_COMMAND.equals(args[0])) {
            stop();
        } else if (START_COMMAND.equals(args[0])) {
            start();
        } else {
            throw new IllegalArgumentException("args is not right");
        }
    }

    private static void stop() {
        running = false;
        CollectorManager.class.notifyAll();
    }

    public static void start() {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:/META-INF/spring/*.xml");
        classPathXmlApplicationContext.start();
        running = true;
        synchronized (CollectorManager.class) {
            while (running) {
                try {
                    CollectorManager.class.wait();
                } catch (Throwable e) {
                }
            }
            classPathXmlApplicationContext.stop();
        }
    }

}
