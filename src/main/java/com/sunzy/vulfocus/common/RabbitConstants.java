package com.sunzy.vulfocus.common;

/**
 * rabbitMQ配置
 */
public class RabbitConstants {
    public static final String DLX_EXCHANGE = "delay_exchange";
    public static final String DLX_ROUTING_KEY = "k1";
    public static final String DLX_QUEUE = "delay_queue1";
    public static final String QUEUE = "delay_queue2";
    public static final String ROUTING_KEY = "k2";
    public static final Integer MESSAGE_TTL = 60 * 30 * 1000;
}
