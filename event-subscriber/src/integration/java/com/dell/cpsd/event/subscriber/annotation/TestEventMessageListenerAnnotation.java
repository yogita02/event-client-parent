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
@EventMessageListener(queues = "test.queue.storage.space.critical")
public class TestEventMessageListenerAnnotation
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
    @EventMessageHandler(capability = EventIntegrationTestCapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_PROFILE_NAME)
    public void handleStorageSpaceCriticalEvent(String message)
    {
        Queue queue = new Queue("test.queue.storage.space.critical.event");
        rabbitAdmin.declareQueue(queue);
        TopicExchange topicExchange = new TopicExchange("test.exchange.storage.space.critical.event");
        rabbitAdmin.declareExchange(topicExchange);
        Binding binding = BindingBuilder.bind((queue)).to(topicExchange).with("test.storage.space.critical.event");
        rabbitAdmin.declareBinding(binding);
        rabbitTemplate.convertAndSend("test.exchange.storage.space.critical.event", "test.storage.space.critical.event",
                "testMessageFromEventHandler");
    }
}
