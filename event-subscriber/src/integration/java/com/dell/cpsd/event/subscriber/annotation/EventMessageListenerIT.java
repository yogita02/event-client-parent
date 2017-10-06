/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.subscriber.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.dell.cpsd.event.client.EventIntegrationTestCapabilityConfiguration;
import com.dell.cpsd.event.client.EventIntegrationTestConfig;
import com.dell.cpsd.hdp.capability.registry.client.binding.config.CapabilityRegistryControlRabbitConfig;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryServiceRabbitConfig;

/**
 * Integration test class for EventMessageListener annotation
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(SpringRunner.class)
@Configuration
@ContextConfiguration(classes = {EventIntegrationTestConfig.class, CapabilityRegistryControlRabbitConfig.class,
        CapabilityRegistryServiceRabbitConfig.class})
public class EventMessageListenerIT
{

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("amqpAdmin")
    private AmqpAdmin      amqpAdmin;

    /**
     * Integration test for event handling with @EventMessageListener at class level and @EventMessageHandler at method level
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEventMessageListener() throws InterruptedException
    {
        rabbitTemplate.convertAndSend(EventIntegrationTestCapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_EXCHANGE,
                EventIntegrationTestCapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_ROUTING_KEY, "testMessage");
        Thread.sleep(5000);
        String responseMsg = (String) rabbitTemplate.receiveAndConvert("test.queue.storage.space.critical.event");
        assertNotNull(responseMsg);
        assertEquals("testMessageFromEventHandler", responseMsg);
    }

    /**
     * Integration test for message handling using @RabbitListener
     * 
     * @throws InterruptedException
     */
    @Test
    public void testMessageHandling() throws InterruptedException
    {
        rabbitTemplate.convertAndSend(EventIntegrationTestCapabilityConfiguration.ADD_STORAGE_REQUEST_EXCHANGE,
                EventIntegrationTestCapabilityConfiguration.ADD_STORAGE_REQUEST_ROUTING_KEY, "testMessageFromMessageHandler");
        Thread.sleep(5000);
        String responseMsg = (String) rabbitTemplate.receiveAndConvert("sample.queue.cpu.utilization");
        assertNotNull(responseMsg);
        assertEquals(responseMsg, "testCPUMessageFromMessageHandler");
    }

    /**
     * Integration test for event handling with @EventMessageListener at method level
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEventMessageListenerAtMethodLevel() throws InterruptedException
    {
        rabbitTemplate.convertAndSend(EventIntegrationTestCapabilityConfiguration.SAMPLE_EVENT_EXCHANGE, EventIntegrationTestCapabilityConfiguration.SAMPLE_EVENT_ROUTING_KEY,
                "testSampleMessage");
        Thread.sleep(5000);
        String responseMsg = (String) rabbitTemplate.receiveAndConvert("test.queue.sample.event.response", 8000);
        assertNotNull(responseMsg);
        assertEquals(responseMsg, "testSampleResponseMessage");
    }
}
