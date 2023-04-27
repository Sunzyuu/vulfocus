package com.sunzy.vulfocus.config;

import com.sunzy.vulfocus.common.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
//@Configuration
public class RabbitConfig {

    /**
     * 死信队列
     * @return {@link Queue}
     */
    @Bean
    public Queue Queue1() {
        Map<String, Object> argments = new HashMap<>();
        argments.put("x-message-ttl", 60000);// 一分钟的延时 测试时使用
        argments.put("x-dead-letter-exchange", RabbitConstants.DLX_EXCHANGE);
        argments.put("x-dead-letter-routing-key", RabbitConstants.ROUTING_KEY);
        return new Queue(RabbitConstants.DLX_QUEUE, true, false, false, argments);
    }


    @Bean
    public Queue Queue2(){
        return new Queue(RabbitConstants.QUEUE, true, false, false);
    }

    /**
     * 交换机
     *
     * @return {@link Exchange}
     */
    @Bean
    public Exchange orderExchange() {
        return new DirectExchange(RabbitConstants.DLX_EXCHANGE, true, false, null);
    }

    /**
     * 路由键
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding Routing() {
        return BindingBuilder.bind(Queue2()).to(orderExchange()).with(RabbitConstants.ROUTING_KEY).noargs();
    }



    /**
     * 死信路由键
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding DlxRouting() {
        return BindingBuilder.bind(Queue1()).to(orderExchange()).with(RabbitConstants.DLX_ROUTING_KEY).noargs();
    }
}
