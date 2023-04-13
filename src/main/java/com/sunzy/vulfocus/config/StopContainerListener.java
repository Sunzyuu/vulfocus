package com.sunzy.vulfocus.config;

import com.rabbitmq.client.Channel;
import com.sunzy.vulfocus.common.RabbitConstants;
import com.sunzy.vulfocus.service.impl.TaskInfoServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class StopContainerListener {

    @Resource
    @Lazy
    private TaskInfoServiceImpl taskInfoService;

    @RabbitListener(queues = RabbitConstants.QUEUE, ackMode = "MANUAL")
    public void getMessage(Message message, Channel channel) throws IOException {
        System.out.println("接收死信队列传递的消息...");
        System.out.println("时间：" + LocalDateTime.now());
        String taskId = new String(message.getBody());
        System.out.println(taskId);
        //
        taskInfoService.stopContainer(taskId);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
