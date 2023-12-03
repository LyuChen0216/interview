package com.lyu.broken.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
/**
 * RabbitMQ配置类
 */
@Configuration
//@DependsOn("myListener")
public class RabbitConfig {
    @Value("exchange.delay")
    private String exchangeName;
    @Value("queue.delay.normal")
    private String queueNormalName;


    @Bean
    public CustomExchange customExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        // CustomExchange(String name, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        return new CustomExchange(exchangeName, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(queueNormalName).build();
    }

    @Bean
    public Binding binding(CustomExchange customExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(customExchange).with("plugins").noargs();
    }

}
