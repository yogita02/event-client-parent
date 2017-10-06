/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.subscriber.annotation;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dell.cpsd.event.subscriber.annotation.postprocessor.EventMessageListenerAnnotationBeanPostProcessor;

/**
 * This class is used to verify that {@link EventMessageListenerAnnotationBeanPostProcessor} handles the messages}
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */
@Component
@RabbitListener(queues = "queue.dell.cpsd.sample.queue.addstorage", containerFactory = "simpleRabbitListenerContainerFactory")
public class TestMessageHandling
{
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("amqpAdmin")
    AmqpAdmin      rabbitAdmin;

    /**
     * Handler method for cpu utilization event
     * 
     * @param message
     *            - Messages sent by the producer
     */
    @RabbitHandler
    public void handleCpuUtilzationEvent(String message)
    {
        Queue queue = new Queue("sample.queue.cpu.utilization");
        rabbitAdmin.declareQueue(queue);
        TopicExchange topicExchange = new TopicExchange("test.exchange.cpu.utilization");
        rabbitAdmin.declareExchange(topicExchange);
        Binding binding = BindingBuilder.bind((queue)).to(topicExchange).with("test.routing.key.cpu.utilization");
        rabbitAdmin.declareBinding(binding);
        rabbitTemplate.convertAndSend("test.exchange.cpu.utilization", "test.routing.key.cpu.utilization",
                "testCPUMessageFromMessageHandler");
    }
}
