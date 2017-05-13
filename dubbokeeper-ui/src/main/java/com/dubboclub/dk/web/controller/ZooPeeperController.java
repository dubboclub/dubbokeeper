package com.dubboclub.dk.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.dubboclub.dk.web.model.SpyZooResponse;
import com.dubboclub.dk.web.model.SpyZooNode;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bieber on 2015/9/24.
 */
@Controller
@RequestMapping("/peeper")
public class ZooPeeperController implements InitializingBean{

    private static final ConcurrentHashMap<String,ZooKeeper> ZK_CLIENT_MAP = new ConcurrentHashMap<String, ZooKeeper>();
    
    private static final Logger logger = LoggerFactory.getLogger(ZooPeeperController.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        String zookeepers = ConfigUtils.getProperty("peeper.zookeepers");
        if(!StringUtils.isEmpty(zookeepers)){
            String[] zookeeperArray = Constants.COMMA_SPLIT_PATTERN.split(zookeepers);
            for(String zk:zookeeperArray){
                ZooKeeper zooKeeper  = new ZooKeeper(zk, Integer.parseInt(ConfigUtils.getProperty("peeper.zookeeper.session.timeout","60000")), new ZkWatcher(zk));
                ZK_CLIENT_MAP.put(zk, zooKeeper);
            }
        }
    }
    @RequestMapping("/listZookeepers.htm")
    public  @ResponseBody
    Collection<String> listZookeepers(){
        return ZK_CLIENT_MAP.keySet();
    }
    
    @RequestMapping("/{zkConnect}/loadChildren.htm")
    public @ResponseBody
    SpyZooResponse listByParent(@RequestParam(value = "parent",defaultValue = "/",required = false)String parent,@PathVariable("zkConnect")String zkConnect){
        SpyZooResponse response = new SpyZooResponse();
        try{
            ZooKeeper zooKeeper  = ZK_CLIENT_MAP.get(zkConnect);
            if(zooKeeper.getState().isAlive()){
                List<String> children = zooKeeper.getChildren(parent,false);
                List<SpyZooNode> nodes = new ArrayList<SpyZooNode>();
                for(String child:children){
                    SpyZooNode node = new SpyZooNode();
                    node.setName(child);
                    node.setDecodeName(URLDecoder.decode(child, "UTF-8"));
                    node.setParent(parent);
                    node.setNodeStat(zooKeeper.exists((parent.equals("/")?"":parent)+"/"+child,false));
                    if(zooKeeper.getChildren((parent.equals("/")?"":parent)+"/"+child,false).size()>0){
                        node.setNodeList(new ArrayList<SpyZooNode>());
                    }else{
                        node.setChildNodes(new ArrayList<SpyZooNode>());
                    }
                    nodes.add(node);
                }
                response.setNodeList(nodes);
            }else{
                response.setState(SpyZooResponse.State.REMOTE_ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("failed to load node {} children",parent);
            response.setState(SpyZooResponse.State.FAILED);
        }
        return response;
    }
    
    
    
    private class ZkWatcher implements Watcher{
        private String host;

        public ZkWatcher(String host) {
            this.host = host;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getState()== Event.KeeperState.Expired||watchedEvent.getState()==Event.KeeperState.Disconnected){
                try {
                    ZooKeeper zooKeeper = ZK_CLIENT_MAP.get(host);
                    if(zooKeeper!=null){
                        try {
                            zooKeeper.close();
                        } catch (InterruptedException e) {
                            //do nothing
                        }
                    }
                    ZK_CLIENT_MAP.put(host,new ZooKeeper(host,Integer.parseInt(ConfigUtils.getProperty("spy.zookeeper.session.timeout","60000")),this));
                } catch (IOException e) {
                    logger.error("failed to reconnect zookeeper server",e);
                }
            }
        }
    }
    
    
    
}
