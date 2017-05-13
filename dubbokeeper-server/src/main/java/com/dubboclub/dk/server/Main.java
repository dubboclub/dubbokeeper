package com.dubboclub.dk.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @date: 2016/1/5.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.server.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class Main {

    private static volatile boolean running=false;

    private static final String STOP_COMMAND="stop";

    private static final String START_COMMAND="start";

    public static void main(String[] args){
        if(args.length==0){
            throw new IllegalArgumentException("args must not be empty");
        }
        if(STOP_COMMAND.equals(args[0])){
            stop();
        }else{
            start();
        }
    }

    private static void stop(){
        running=false;
        Main.class.notifyAll();
    }

    private static void start(){
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:/META-INF/spring/*.xml");
        classPathXmlApplicationContext.start();
        running=true;
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
            classPathXmlApplicationContext.stop();
        }
    }

}
