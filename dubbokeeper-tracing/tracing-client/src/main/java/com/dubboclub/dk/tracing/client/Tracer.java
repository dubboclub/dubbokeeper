package com.dubboclub.dk.tracing.client;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.BinaryAnnotation;
import com.dubboclub.dk.tracing.api.Endpoint;
import com.dubboclub.dk.tracing.api.Span;
import com.dubboclub.dk.tracing.client.util.GUId;
import com.dubboclub.dk.tracing.client.util.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/8.
 */
public class Tracer {

    private static Logger logger = LoggerFactory.getLogger(Tracer.class);

    private SyncTransfer syncTransfer = ExtensionLoader.getExtensionLoader(SyncTransfer.class)
            .getExtension(ConfigUtils.getProperty(DstConstants.SYNC_TRANSFER_TYPE,DstConstants.DEFAULT_SYNC_TRANSFER));

    public void init(){
        syncTransfer.start();
    }



    public void beforeInvoke(boolean isConsumerSide) {
        if (isConsumerSide) {
            Long traceId = createConsumerSideTraceId();
            if(traceId!=null){
                createConsumerSideSpan();
                addClientSendAnnotation();
            }
        } else{
            createProvideSideTraceId();
            createProviderSideSpan();
            addServerReceiveAnnotation();
        }

        setAttachment();
        logger.debug("{}. add attachment...", isConsumerSide ? 4 : 8);
        logger.debug("before RpcContext : {}", RpcContext.getContext().getAttachments());
    }

    public void afterInvoke(boolean isConsumerSide) {
        if (isConsumerSide) {
            addClientReceiveAnnotation();
        } else{
            addServerSendAnnotation();
        }
        send();
        logger.debug("{}. send span...", isConsumerSide ? 12 : 10);
        logger.debug("after RpcContext : {}", RpcContext.getContext().getAttachments());
    }

    private void send() {
        //弹出栈顶span
        Span span = ContextHolder.popSpan();
        if (span != null) {
            syncTransfer.syncSend(span);
        }
    }

    public void addException(Throwable throwable) {
        Span span = ContextHolder.getSpan();
        if (span != null) {
            Endpoint endpoint = createEndpoint();
            BinaryAnnotation annotation = new BinaryAnnotation();
            annotation.setKey(DstConstants.EXCEPTION);
            annotation.setType(throwable.getClass().getName());
            annotation.setValue(throwable.getMessage());
            annotation.setHost(endpoint);
            span.addAnnotation(annotation);
        }
    }

    private Span createConsumerSideSpan() {
        if(ContextHolder.isSample()){
            Span span = new Span();
            span.setId(GUId.singleton().nextId());
            Span parentSpan = ContextHolder.getSpan();
            if (parentSpan != null) {
                span.setParentId(parentSpan.getId());
                span.setTraceId(parentSpan.getTraceId());
            } else {
                span.setTraceId(ContextHolder.getTraceId());
            }
            span.setServiceName(getServiceName());
            span.setName(getMethodName());
            ContextHolder.setSpan(span);
            logger.debug("2. create consumer side span: {}", span);
        }
        return ContextHolder.getSpan();
    }

    private Span createProviderSideSpan() {
        RpcContext rpcContext = RpcContext.getContext();
        String traceId = rpcContext.getAttachment(DstConstants.DST_TRACE_ID);
        String spanId = rpcContext.getAttachment(DstConstants.DST_SPAN_ID);
        String parentSpanId = rpcContext.getAttachment(DstConstants.DST_PARENT_SPAN_ID);
        if (StringUtils.isNotEmpty(traceId) && StringUtils.isNotEmpty(spanId)) {//只需要判断traceId和spanid即可
            Span span = new Span();
            span.setId(Long.parseLong(spanId));
            span.setParentId("null".equalsIgnoreCase(parentSpanId) ? null : Long.parseLong(parentSpanId));
            span.setTraceId(Long.parseLong(traceId));
            span.setServiceName(getServiceName());
            span.setName(getMethodName());
            ContextHolder.setSpan(span);
            logger.debug("6. create provider side span: {}", span);
        }

        return ContextHolder.getSpan();
    }

    private Long createConsumerSideTraceId() {
        Long traceId = ContextHolder.getTraceId();
        if (traceId == null) {//启动一个新的链路
            if (ContextHolder.isSample() && Sampler.isSample(getServiceName())) {
                ContextHolder.setTraceId(GUId.singleton().nextId());
            } else {
                ContextHolder.setLocalSample(false);
            }
        }
        logger.debug("1. create consumer side trace id: {}", ContextHolder.getTraceId());
        return ContextHolder.getTraceId();
    }

    private Long createProvideSideTraceId() {
        RpcContext rpcContext = RpcContext.getContext();
        String isSample = rpcContext.getAttachment(DstConstants.DST_IS_SAMPLE);
        if(StringUtils.isNotEmpty(isSample)){
            ContextHolder.setLocalSample(Boolean.valueOf(isSample));
        }
        String traceId = rpcContext.getAttachment(DstConstants.DST_TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            ContextHolder.setTraceId(GUId.singleton().nextId());
        } else {
            ContextHolder.setTraceId(Long.parseLong(traceId));
        }
        logger.debug("5. create provider side trace id: {}", ContextHolder.getTraceId());
        return ContextHolder.getTraceId();
    }

    private void setAttachment() {
        RpcContext rpcContext = RpcContext.getContext();
        Long traceId = ContextHolder.getTraceId();
        rpcContext.setAttachment(DstConstants.DST_IS_SAMPLE,ContextHolder.isSample()+"");
        if (traceId != null) {
            rpcContext.setAttachment(DstConstants.DST_TRACE_ID, String.valueOf(traceId));
        }
        Span span = ContextHolder.getSpan();
        if (span != null) {
            rpcContext.setAttachment(DstConstants.DST_SPAN_ID, String.valueOf(span.getId()));
            rpcContext.setAttachment(DstConstants.DST_PARENT_SPAN_ID, String.valueOf(span.getParentId()));
        }
    }

    private Endpoint createEndpoint() {
        Endpoint endpoint = new Endpoint();
        endpoint.setApplicationName(getApplicationName());
        endpoint.setIp(getIp());
        endpoint.setPort(getPort());
        return endpoint;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void addClientSendAnnotation() {
        logger.debug("3. create client side annotation: {}", Annotation.CLIENT_SEND);
        addAnnotation(Annotation.CLIENT_SEND);
    }

    private void addClientReceiveAnnotation() {
        logger.debug("11. create client side annotation: {}", Annotation.CLIENT_RECEIVE);
        addAnnotation(Annotation.CLIENT_RECEIVE);
    }

    private void addServerSendAnnotation() {
        logger.debug("9. create server side annotation: {}", Annotation.SERVER_SEND);
        addAnnotation(Annotation.SERVER_SEND);
    }

    private void addServerReceiveAnnotation() {
        logger.debug("7. create server side annotation: {}", Annotation.SERVER_RECEIVE);
        addAnnotation(Annotation.SERVER_RECEIVE);
    }

    private void addAnnotation(String value) {
        Span span = ContextHolder.getSpan();
        if (span != null) {
            Endpoint endpoint = createEndpoint();
            Annotation annotation = new Annotation();
            annotation.setValue(value);
            annotation.setHost(endpoint);
            annotation.setTimestamp(currentTimeMillis());
            span.addAnnotation(annotation);
            logger.debug("add annotation({}) for span({})...", value, span.getId());
        }
    }

    private String getApplicationName() {
        return RpcContext.getContext().getUrl().getParameter(Constants.APPLICATION_KEY);
    }

    private String getServiceName() {
        return RpcContext.getContext().getUrl().getServiceInterface();
    }

    private String getMethodName() {
        return RpcContext.getContext().getMethodName();
    }

    private String getIp() {
        return RpcContext.getContext().getLocalHost();
    }

    private Integer getPort() {
        return RpcContext.getContext().getLocalPort();
    }

}
