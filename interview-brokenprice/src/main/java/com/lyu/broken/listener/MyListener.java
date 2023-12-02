package com.lyu.broken.listener;

import com.lyu.broken.service.BrokenPriceService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
/**
* RabbitMQ的Listener
* */
@Slf4j
@Component
public class MyListener {
    @Resource
    private BrokenPriceService brokenPriceService;

    @RabbitListener(queues = "queue.delay.normal", ackMode = "MANUAL")
    public void receiveMsg(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            // 手动确认消息已被处理
            if (shouldProcessMessage(message)) {
                processMessage(message);
                channel.basicAck(deliveryTag, false);
            } else {
                // 将used属性改为0，拒绝消息并将其从队列中移除
                brokenPriceService.updateUrlStatus(message,0);
                System.out.println("Ignoring message: " + message);
                channel.basicReject(deliveryTag, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private boolean shouldProcessMessage(String message) {
        Integer used = brokenPriceService.getUrlStatus(message);
        if (used != 2 || message.isEmpty()) {
            return false;
        }
        return true;
    }

    private void processMessage(String message) {
        System.out.println("Processing message: " + message);
    }
}

