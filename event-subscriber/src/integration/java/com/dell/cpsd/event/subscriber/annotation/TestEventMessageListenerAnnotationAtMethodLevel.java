/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.subscriber.annotation;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dell.cpsd.event.client.EventIntegrationTestCapabilityConfiguration;
import com.dell.cpsd.event.subscriber.annotation.postprocessor.EventMessageListenerAnnotationBeanPostProcessor;

/**
 * This class is used to verify that {@link EventMessageListenerAnnotationBeanPostProcessor} handles the event messages}
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */
@Component
public class TestEventMessageListenerAnnotationAtMethodLevel
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
    @EventMessageListener(queues = "test.queue.sample.event", capability = EventIntegrationTestCapabilityConfiguration.SAMPLE_EVENT_PROFILE_NAME)
    public void handleCpuUtilzationEvent(String message)
    {
        Queue queue = new Queue("test.queue.sample.event.response");
        rabbitAdmin.declareQueue(queue);
        TopicExchange topicExchange = new TopicExchange("test.exchange.sample.event");
        rabbitAdmin.declareExchange(topicExchange);
        Binding binding = BindingBuilder.bind((queue)).to(topicExchange).with("test.routing.key.sample.event");
        rabbitAdmin.declareBinding(binding);
        rabbitTemplate.convertAndSend("test.exchange.sample.event", "test.routing.key.sample.event", "testSampleResponseMessage");
    }
}
