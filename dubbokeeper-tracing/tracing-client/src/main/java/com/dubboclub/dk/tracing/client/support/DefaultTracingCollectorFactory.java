package com.dubboclub.dk.tracing.client.support;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.cluster.Cluster;
import com.alibaba.dubbo.rpc.cluster.directory.StaticDirectory;
import com.alibaba.dubbo.rpc.cluster.support.AvailableCluster;
import com.dubboclub.dk.tracing.api.TracingCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * DefaultTracingCollectorFactory
 * Created by bieber.bibo on 16/7/18
 */

public class DefaultTracingCollectorFactory extends AbstractTracingCollectorFactory {

    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    private static final Cluster cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getAdaptiveExtension();

    @Override
    protected TracingCollector createTracingCollector(List<URL> urls) {

        Invoker<TracingCollector> invoker;
        if(urls.size()==1){
            invoker = protocol.refer(TracingCollector.class,urls.get(0));
        }else{
            List<Invoker<TracingCollector>> invokers = new ArrayList<Invoker<TracingCollector>>();
            URL registryURL = null;
            for (URL url : urls) {
                invokers.add(protocol.refer(TracingCollector.class, url));
                if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {
                    registryURL = url; // 用了最后一个registry url
                }
            }
            if (registryURL != null) { // 有 注册中心协议的URL
                // 对有注册中心的Cluster 只用 AvailableCluster
                URL u = registryURL.addParameter(Constants.CLUSTER_KEY, AvailableCluster.NAME);
                invoker = cluster.join(new StaticDirectory(u, invokers));
            }  else { // 不是 注册中心的URL
                invoker = cluster.join(new StaticDirectory(invokers));
            }
        }
        TracingCollector tracingCollector = proxyFactory.getProxy(invoker);
        return tracingCollector;
    }


}
