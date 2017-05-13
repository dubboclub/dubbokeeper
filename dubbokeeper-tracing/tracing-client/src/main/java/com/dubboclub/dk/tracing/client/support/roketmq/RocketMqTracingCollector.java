package com.dubboclub.dk.tracing.client.support.roketmq;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.dubboclub.dk.tracing.api.Span;
import com.dubboclub.dk.tracing.api.TracingCollector;
import com.dubboclub.dk.tracing.client.DstConstants;
import java.util.List;

/**
 * RocketMqTracingCollector
 * Created by bieber.bibo on 16/8/11
 */

public class RocketMqTracingCollector implements TracingCollector {

    private DefaultMQProducer defaultMQProducer;

    private Logger logger = LoggerFactory.getLogger(RocketMqTracingCollector.class);

    public RocketMqTracingCollector() {
        defaultMQProducer = new DefaultMQProducer(DstConstants.ROCKET_MQ_PRODUCER);
        defaultMQProducer.setNamesrvAddr(ConfigUtils.getProperty(DstConstants.ROCKET_MQ_NAME_SRV_ADD));
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            throw new IllegalArgumentException("fail to start rocketmq producer.",e);
        }
    }

    @Override
    public void push(List<Span> spanList) {
        byte[] bytes = JSON.toJSONBytes(spanList);
        Message message = new Message(DstConstants.ROCKET_MQ_TOPIC,bytes);
        try {
            SendResult sendResult = defaultMQProducer.send(message);
            if(sendResult.getSendStatus()!= SendStatus.SEND_OK){
                logger.error("send mq message return ["+sendResult.getSendStatus()+"]");
            }
        } catch (Exception e) {
            logger.error("fail to send message.",e);
        }
    }
}
