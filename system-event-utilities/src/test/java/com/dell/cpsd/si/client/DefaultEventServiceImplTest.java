/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.si.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.dell.cpsd.event.client.DefaultEventServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEventServiceImplTest
{

    @InjectMocks
    private DefaultEventServiceImpl classUnderTest;
    @Mock
    private RabbitTemplate   rabbitTemplate;
    private String           eventExchange;
    private String           eventRoutingKey;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        eventExchange = "test-event-exchange";
        eventRoutingKey = "test-event-routing-key";
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        eventExchange = null;
        eventRoutingKey = null;
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * This tests to throw {@link IllegalArgumentException} when event exchange is null.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEventExchange()
    {
        Object event = new Object();
        classUnderTest.triggerEvent(null, eventRoutingKey, event);
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * This tests to throw {@link IllegalArgumentException} when event exchange is empty.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForEmptyEventExchange()
    {
        Object event = new Object();
        classUnderTest.triggerEvent("", eventRoutingKey, event);
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * This tests to throw {@link IllegalArgumentException} when event routing key is null.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEventRoutingKey()
    {
        Object event = new Object();
        classUnderTest.triggerEvent(eventExchange, null, event);
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * This tests to throw {@link IllegalArgumentException} when event routing key is empty.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForEmptyEventRoutingKey()
    {
        Object event = new Object();
        classUnderTest.triggerEvent(eventExchange, "", event);
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * This tests to throw {@link IllegalArgumentException} when event is null.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testTriggerEventForNullEvent()
    {
        classUnderTest.triggerEvent(eventExchange, eventRoutingKey, null);
    }

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * Method to test valid event is sent onto the message bus.
     * 
     */
    @Test
    public final void testTriggerEventForValidEvent()
    {
        Object event = new Object();
        classUnderTest.triggerEvent(eventExchange, eventRoutingKey, event);
        Mockito.verify(rabbitTemplate).convertAndSend(eventExchange, eventRoutingKey, event);
    }

}
