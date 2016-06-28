package com.dubboclub.startup;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by bieber on 2015/5/26.
 */
public class JettyServer {
    
    public static void main(String[] args) throws Exception {
        final Server server = new Server(8088);
        WebAppContext webAppContext = new WebAppContext("dubbokeeper-ui/src/main/webapp", "/");
        webAppContext.setDescriptor("dubbokeeper-ui/src/main/webapp/WEB-INF/web.xml");
        webAppContext.setMaxFormContentSize(1024*1024*60);
        server.setHandler(webAppContext);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        server.start();
        server.join();
    }
}
