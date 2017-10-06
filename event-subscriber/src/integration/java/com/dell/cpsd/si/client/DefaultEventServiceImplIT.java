/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.si.client;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.dell.cpsd.event.client.EventService;
import com.dell.cpsd.hdp.capability.registry.client.binding.config.CapabilityRegistryControlRabbitConfig;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryServiceRabbitConfig;

@RunWith(SpringRunner.class)
@Configuration
@ContextConfiguration(classes = {IntegrationTestConfig.class, CapabilityRegistryControlRabbitConfig.class,
        CapabilityRegistryServiceRabbitConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefaultEventServiceImplIT
{
    @Autowired
    @Qualifier("capabilityRegistryServiceAmqpAdmin")
    private AmqpAdmin     amqpAdmin;
    @Autowired
    private EventService  eventService;
    private String        eventExchangeName;
    private String        eventRoutingKey;
    private TopicExchange eventExchange;
    private Queue         eventQueue;
    private String        eventQueueName;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        eventQueueName = "queue.dell.spsd.sample.test.event";
        eventExchangeName = "exchange.dell.cpsd.sample.test.event";
        eventRoutingKey = "dell.cpsd.sample.test.event";
        eventQueue = new Queue(eventQueueName);
        eventExchange = new TopicExchange(eventExchangeName);
        this.amqpAdmin.declareExchange(eventExchange);
        this.amqpAdmin.declareQueue(eventQueue);
        this.amqpAdmin.declareBinding(BindingBuilder.bind(eventQueue).to(eventExchange).with(eventRoutingKey));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        amqpAdmin.deleteExchange(eventExchangeName);
        amqpAdmin.deleteQueue(eventQueueName);
        eventQueueName = null;
        eventExchangeName = null;
        eventRoutingKey = null;
        eventExchange = null;
        eventQueue = null;
    }

    /**
     * This tests to throw {@link IllegalArgumentException} when event exchange is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEventExchange()
    {
        Object event = new Object();
        eventService.triggerEvent(null, eventRoutingKey, event);
    }

    /**
     * This tests to throw {@link IllegalArgumentException} when event exchange is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForEmptyEventExchange()
    {
        Object event = new Object();
        eventService.triggerEvent("", eventRoutingKey, event);
    }

    /**
     * This tests to throw {@link IllegalArgumentException} when event routing key is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEventRoutingKey()
    {
        Object event = new Object();
        eventService.triggerEvent(eventExchangeName, null, event);
    }

    /**
     * This tests to throw {@link IllegalArgumentException} when event routing key is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForEmptyEventRoutingKey()
    {
        Object event = new Object();
        eventService.triggerEvent(eventExchangeName, "", event);
    }

    /**
     * This tests to throw {@link IllegalArgumentException} when event is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEvent()
    {
        eventService.triggerEvent(eventExchangeName, eventRoutingKey, null);
    }

    /**
     * Method to test valid event is sent onto the message bus.
     */
    @Test
    public final void testTriggerEventForValidEvent()
    {
        eventService.triggerEvent(eventExchangeName, eventRoutingKey, sampleEvent());
    }

    private String sampleEvent()
    {
        return "Sample Event Triggered";
    }
}
