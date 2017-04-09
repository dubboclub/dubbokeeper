package com.dubboclub.dk.tracing.client;

/**
 * Created by zetas on 2016/7/10.
 */
public class DstConstants {

    public static final String DST_TRACE_ID = "~dstTraceId";
    public static final String DST_SPAN_ID = "~dstSpanId";
    public static final String DST_PARENT_SPAN_ID = "~dstParentSpanId";
    public static final String DST_IS_SAMPLE ="~dstIsSampler";

    //properties配置同步trace类型的配置key
    public static final String SYNC_TRANSFER_TYPE="trace.sync.transfer.type";

    //默认的同步方式
    public static final String DEFAULT_SYNC_TRANSFER="default";

    public static final String DEFAULT_COLLECTOR_TYPE="default";

    public static final String TRACING_COLLECTOR="tracing.collector.type";


    public static final String FLUSH_SIZE_KEY="trace.flush.size";

    public static final String QUEUE_SIZE_KEY="trace.queue.buffer.size";

    public static final String DEFAULT_FLUSH_SIZE="1024";

    public static final String DEFAULT_BUFFER_QUEUE_SIZE="1024";

    public static final String EXCEPTION = "exception";

    public static final String ROCKET_MQ_NAME_SRV_ADD="rocket_mq_name_srv_add";

    public static final String ROCKET_MQ_TOPIC="dst_span_topic";

    public static final String ROCKET_MQ_PRODUCER="dst_span_producer";

}
