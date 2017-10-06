/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.si.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.dell.cpsd.event.client.DefaultEventServiceImpl;
import com.dell.cpsd.event.exceptions.EventSubscriptionException;
import com.dell.cpsd.hdp.capability.registry.amqp.config.CapabilitiesAmqpEntitiesConfig;
import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityService;
import com.dell.cpsd.hdp.capability.registry.exceptions.CapabilityBindingException;
import com.dell.cpsd.hdp.capability.registry.exceptions.CapabilityRetrievalException;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEventServiceImplTest
{

    @InjectMocks
    private DefaultEventServiceImpl classUnderTest;
    @Mock
    private RabbitTemplate          rabbitTemplate;
    private String                  eventExchange;
    private String                  eventRoutingKey;
    @Mock
    ICapabilityService              capabilityService;

    @Mock
    CapabilitiesAmqpEntitiesConfig  capabilitiesAmqpEntitiesConfigMock;

    @Captor
    ArgumentCaptor<String>          exchangeNameCaptor;

    @Captor
    ArgumentCaptor<String>          exchangeTypeCaptor;

    @Captor
    ArgumentCaptor<String>          exchangeRoutingKeyCaptor;

    @Captor
    ArgumentCaptor<List<Queue>>     queueCaptor;

    Capability                      capability;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        eventExchange = "test-event-exchange";
        eventRoutingKey = "test-event-routing-key";
        capability = createCapability();
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

    /**
     * Test method for
     * {@link com.dell.cpsd.si.client.DefaultEventServiceImpl#triggerEvent(java.lang.String, java.lang.String, java.lang.Object)}.
     * 
     * Method to test valid event is sent onto the message bus.
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * @throws CapabilityRetrievalException
     * 
     */
    @Test
    public final void testSubscribeToEvent() throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        Mockito.when(capabilityService.getCapability(Mockito.anyString())).thenReturn(capability);
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "cpu-utilization-event");
        Mockito.verify(capabilityService).getCapability("cpu-utilization-event");
        Mockito.verify(capabilitiesAmqpEntitiesConfigMock).createAndBindExchange(exchangeNameCaptor.capture(), exchangeTypeCaptor.capture(),
                exchangeRoutingKeyCaptor.capture(), queueCaptor.capture());
        assertEquals("test-event-exchange", exchangeNameCaptor.getValue());
        assertEquals(ExchangeTypes.TOPIC, exchangeTypeCaptor.getValue());
        assertEquals("test-event-routing-key", exchangeRoutingKeyCaptor.getValue());
        assertEquals("testQueue", queueCaptor.getValue().get(0).getName());

    }

    /**
     * Negative - Verify that {@link IllegalArgumentException} will be thrown when queue is null
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSubscribeToEventWithNullQueue()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        classUnderTest.subscribeToEvent(null, "cpu-utilization-event");
    }

    /**
     * Negative - Verify that {@link IllegalArgumentException} will be thrown when queue name is empty
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSubscribeToEventWithEmptyQueueName()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        classUnderTest.subscribeToEvent(Arrays.asList(""), "cpu-utilization-event");
    }

    /**
     * Negative - Verify that {@link IllegalArgumentException} will be thrown when queues list is empty
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSubscribeToEventWithEmptyQueuesList()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        classUnderTest.subscribeToEvent(new ArrayList<String>(), "cpu-utilization-event");
    }

    /**
     * Negative - Verify that {@link IllegalArgumentException} will be thrown when capability name is empty
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSubscribeToEventWithEmptyCapabilityName()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "");
    }

    /**
     * Negative - Verify that {@link IllegalArgumentException} will be thrown when capability name is null
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSubscribeToEventWithNullCapabilityName()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), null);
    }

    /**
     * Negative - Verify that EventSubscriptionException is thrown when Provider Endpoint is not available in capability object.
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = EventSubscriptionException.class)
    public final void testSubscribeToEventWithoutProviderEndpoint()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        capability.setProviderEndpoint(null);
        Mockito.when(capabilityService.getCapability(Mockito.anyString())).thenReturn(capability);
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "cpu-utilization-event");
        Mockito.verify(capabilityService).getCapability("cpu-utilization-event");
    }

    /**
     * Negative - Verify that EventSubscriptionException is thrown when Provider Endpoint properties is not available in capability object.
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = EventSubscriptionException.class)
    public final void testSubscribeToEventWithoutProviderEndpointProperties()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        capability.getProviderEndpoint().setEndpointProperties(null);
        Mockito.when(capabilityService.getCapability(Mockito.anyString())).thenReturn(capability);
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "cpu-utilization-event");
        Mockito.verify(capabilityService).getCapability("cpu-utilization-event");
    }

    /**
     * Negative - Verify that EventSubscriptionException is thrown when Provider Endpoint properties in capability object is empty.
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = EventSubscriptionException.class)
    public final void testSubscribeToEventWithEmptyProviderEndpointProperties()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        capability.getProviderEndpoint().getEndpointProperties().clear();
        Mockito.when(capabilityService.getCapability(Mockito.anyString())).thenReturn(capability);
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "cpu-utilization-event");
        Mockito.verify(capabilityService).getCapability("cpu-utilization-event");
    }

    /**
     * Negative - Verify that EventSubscriptionException is thrown when exception occurs on invocation of createAndBindExchange method
     * 
     * @throws CapabilityRetrievalException
     * @throws CapabilityBindingException
     * @throws EventSubscriptionException
     * 
     */
    @Test(expected = EventSubscriptionException.class)
    public final void testSubscribeToEventWithExceptionOnCreatAndBindExchange()
            throws CapabilityRetrievalException, CapabilityBindingException, EventSubscriptionException
    {
        Mockito.when(capabilityService.getCapability(Mockito.anyString())).thenReturn(capability);
        Mockito.doThrow(CapabilityBindingException.class).when(capabilitiesAmqpEntitiesConfigMock).createAndBindExchange(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
        classUnderTest.subscribeToEvent(Arrays.asList("testQueue"), "cpu-utilization-event");
        Mockito.verify(capabilityService).getCapability("cpu-utilization-event");
        Mockito.verify(capabilitiesAmqpEntitiesConfigMock).createAndBindExchange(exchangeNameCaptor.capture(), exchangeTypeCaptor.capture(),
                exchangeRoutingKeyCaptor.capture(), queueCaptor.capture());
    }

    private Capability createCapability()
    {
        Capability capability = new Capability();
        capability.setProviderEndpoint(createProviderEndpoint());
        return capability;
    }

    private ProviderEndpoint createProviderEndpoint()
    {
        ProviderEndpoint providerEndpoint = new ProviderEndpoint();
        List<EndpointProperty> endpointProperties = new ArrayList<EndpointProperty>();
        EndpointProperty exchangeEndpointProperty = new EndpointProperty("event-exchange", "test-event-exchange");
        EndpointProperty routingKeyEndpointProperty = new EndpointProperty("event-routing-key", "test-event-routing-key");
        EndpointProperty exchangeTypeEndpointProperty = new EndpointProperty("event-exchange-type", ExchangeTypes.TOPIC);
        endpointProperties.add(exchangeEndpointProperty);
        endpointProperties.add(routingKeyEndpointProperty);
        endpointProperties.add(exchangeTypeEndpointProperty);
        providerEndpoint.setEndpointProperties(endpointProperties);
        providerEndpoint.setType(ExchangeTypes.TOPIC);
        return providerEndpoint;
    }

}
