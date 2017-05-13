package com.dubboclub.dk.tracing.client.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.dubboclub.dk.tracing.api.TracingCollector;
import com.dubboclub.dk.tracing.client.ContextHolder;
import com.dubboclub.dk.tracing.client.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/8.
 */
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class TracingFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(TracingFilter.class);

    private Tracer tracer = new Tracer();

    public TracingFilter() {
        tracer.init();
    }

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (invoker.getInterface() == TracingCollector.class) {
            return invoker.invoke(invocation);
        }
        boolean isConsumerSide = isConsumerSide();
        try {
            tracer.beforeInvoke(isConsumerSide);
            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                tracer.addException(result.getException());
            }
            return result;
        } catch (RpcException e) {
            tracer.addException(e);
            throw e;
        } finally {
            tracer.afterInvoke(isConsumerSide);
            ContextHolder.removeAll();
        }
    }

    private boolean isConsumerSide() {
        URL url = RpcContext.getContext().getUrl();
        return Constants.CONSUMER_SIDE.equals(url.getParameter(Constants.SIDE_KEY));
    }

}
