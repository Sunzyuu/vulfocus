package com.sunzy.vulfocus.utils;


import com.sunzy.vulfocus.common.RabbitConstants;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
class RabbitMQTest {

    @Resource
    private AmqpTemplate amqpTemplate;



    @Test
    void testSendAmqp() {
        String taskId = "cfdf21cd13f248d3885ec47329bbb3bf";
        amqpTemplate.convertAndSend(RabbitConstants.DLX_EXCHANGE,
                RabbitConstants.DLX_ROUTING_KEY,
                taskId);
        System.out.println("发送时间：" + LocalDateTime.now());
        System.out.println("send message success");
    }

    @Test
    void testSendAmqp1() {
        String taskId = "111342342";
        amqpTemplate.convertAndSend( "queue",
                taskId);
        System.out.println("send message success");
    }

    //
//    @Resource
//    private RabbitMqProductor rabbitMQProducer;
//
//    @Resource
//    private RabbitMqConsumer rabbitMqConsumer;
//
//
//    @Test
//    void testRabbitMqProductor() throws InterruptedException {
//        for (int i = 0; i < 2; i++) {
//            Thread.sleep(1000);
//            rabbitMQProducer.product();
//        }
//    }




//    @Test
//    void testProductor() throws Exception {
//        String QUEUE_NAME = "queue";
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//
//        connectionFactory.setHost("127.0.0.1");
//        //连接端口;默认为 5672
//        connectionFactory.setPort(5672);
//        //虚拟主机名称;默认为 /
//        connectionFactory.setVirtualHost("host1");
//        //连接用户名；默认为guest
//        connectionFactory.setUsername("root");
//        //连接密码；默认为guest
//        connectionFactory.setPassword("123456");
//
//        //创建连接
//        Connection connection = connectionFactory.newConnection();
//
//        // 创建频道
//        Channel channel = connection.createChannel();
//        channel.queueDeclare(RabbitConstants.ORDER_DLX_QUEUE, true, false, false, null);
//
//        // 声明（创建）队列
//        /**
//         * 参数1：队列名称
//         * 参数2：是否定义持久化队列
//         * 参数3：是否独占本次连接
//         * 参数4：是否在不使用的时候自动删除队列
//         * 参数5：队列其它参数
//         /*
//
//        // 要发送的信息
//        /**
//         * 参数1：交换机名称，如果没有指定则使用默认Default Exchage
//         * 参数2：路由key,简单模式可以传递队列名称
//         * 参数3：消息其它属性
//         * 参数4：消息内容
//         */
//        String message = "hi man！";
//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//        for (int i = 0; i < 5; i++) {
//            System.out.println("已发送消息：" + message);
//        }
//        // 关闭资源
//        channel.close();
//        connection.close();
//    }


    /*@Test
    void testConsumer() throws Exception {
        String QUEUE_NAME = "queue";
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("127.0.0.1");
        //连接端口;默认为 5672
        connectionFactory.setPort(5672);
        //虚拟主机名称;默认为 /
        connectionFactory.setVirtualHost("host1");
        //连接用户名；默认为guest
        connectionFactory.setUsername("root");
        //连接密码；默认为guest
        connectionFactory.setPassword("123456");

        //创建连接
        Connection connection = connectionFactory.newConnection();

        // 创建频道
        Channel channel = connection.createChannel();
        // 接收消息
        DefaultConsumer consumer = new DefaultConsumer(channel){
            *//**
             * consumerTag 消息者标签，在channel.basicConsume时候可以指定
             * envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志(收到消息失败后是否需要重新发送)
             * properties 属性信息
             * body 消息
             *//*
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException, UnsupportedEncodingException {
                //路由key
                System.out.println("路由key为：" + envelope.getRoutingKey());
                //交换机
                System.out.println("交换机为：" + envelope.getExchange());
                //消息id
                System.out.println("消息id为：" + envelope.getDeliveryTag());
                //收到的消息
                System.out.println("接收到的消息为：" + new String(body, "utf-8"));
            }
        };
        //监听消息
        *//**
         * 参数1：队列名称
         * 参数2：是否自动确认，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复
         会删除消息，设置为false则需要手动确认
         * 参数3：消息接收到后回调
         *//*
        channel.basicConsume(QUEUE_NAME, true, consumer);

        //不关闭资源，应该一直监听消息
        // 关闭资源
//        channel.close();
//        connection.close();

    }*/

}