/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.dell.cpsd.event.exceptions.EventSubscriptionException;
import com.dell.cpsd.hdp.capability.registry.amqp.config.CapabilitiesAmqpEntitiesConfig;
import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityService;
import com.dell.cpsd.hdp.capability.registry.client.builder.AmqpProviderEndpointBuilder;
import com.dell.cpsd.hdp.capability.registry.exceptions.CapabilityBindingException;
import com.dell.cpsd.hdp.capability.registry.exceptions.CapabilityRetrievalException;

/**
 * EventServiceImpl - This class provides event related api.
 *
 * @see DefaultEventServiceImpl#triggerEvent(String, String, Object)
 */
@Service("eventService")
public class DefaultEventServiceImpl implements EventService
{

    private static final String            DATA_IN_PROVIDER_ENDPOINT_IS_NULL_OR_EMPTY_MESSAGE = "Data in Provider endpoint is null/empty";

    @Autowired
    private ICapabilityService             capabilityService;

    @Autowired
    private CapabilitiesAmqpEntitiesConfig capabilitiesAmqpEntitiesConfig;

    @Autowired
    private RabbitTemplate                 rabbitTemplate;

    @Override
    public void triggerEvent(String eventExchange, String eventRoutingKey, Object event)
    {
        Assert.hasText(eventExchange, "Exchange name cannot be empty");
        Assert.hasText(eventRoutingKey, "Routing key cannot be empty");
        Assert.notNull(event, "Event cannot be empty");
        this.rabbitTemplate.convertAndSend(eventExchange, eventRoutingKey, event);
    }

    @Override
    public void subscribeToEvent(List<String> queues, String eventCapabilityName) throws EventSubscriptionException

    {
        Assert.isTrue(!CollectionUtils.isEmpty(queues), "Queue name cannot be null/empty.");
        String queueWithoutName = queues.stream().filter(Objects::nonNull).filter(queue -> !StringUtils.hasText(queue)).findFirst()
                .orElse(null);
        Assert.isNull(queueWithoutName, "Queue name cannot be null/empty.");
        Assert.hasText(eventCapabilityName, "Event capability name cannot be null/empty.");
        Capability eventCapability = getCapability(eventCapabilityName);
        bindQueueAndEventExchange(queues, eventCapability);

    }

    private Capability getCapability(String capabilityName) throws EventSubscriptionException
    {
        Capability capability = null;
        try
        {
            capability = capabilityService.getCapability(capabilityName);
        }
        catch (CapabilityRetrievalException exception)
        {
            throw new EventSubscriptionException(exception);
        }
        return capability;
    }

    /**
     * Extracts the event exchange,event routing key,event exchange type from the capability.Checks if the provided queue is already
     * existing.If not, a new queue is created and bound to the event exchange with the event routing key.
     * 
     * @param queues
     *            - List of queues that needs to be bound to event exchange.
     * @param capability
     *            - This is retrieved from capability registry.This contains all the event endpoint properties
     * @throws EventSubscriptionException
     *             if event endpoint properties are not valid or if queues could not be bound to event exchange
     */
    private void bindQueueAndEventExchange(List<String> queues, Capability capability) throws EventSubscriptionException
    {

        Queue eventQueue = null;
        String eventExchange = null;
        String eventRoutingKey = null;
        String eventExchangeType = null;
        if (null != capability)
        {
            ProviderEndpoint providerEndpoint = capability.getProviderEndpoint();
            if (null != providerEndpoint && !CollectionUtils.isEmpty(providerEndpoint.getEndpointProperties()))
            {
                List<EndpointProperty> endpointProperties = providerEndpoint.getEndpointProperties();
                eventExchange = getEndpointPropertyValue(endpointProperties, AmqpProviderEndpointBuilder.EVENT_EXCHANGE);
                eventRoutingKey = getEndpointPropertyValue(endpointProperties, AmqpProviderEndpointBuilder.EVENT_ROUTING_KEY);
                eventExchangeType = getEndpointPropertyValue(endpointProperties, AmqpProviderEndpointBuilder.EVENT_EXCHANGE_TYPE);
            }
            else
            {
                throw new EventSubscriptionException(DATA_IN_PROVIDER_ENDPOINT_IS_NULL_OR_EMPTY_MESSAGE);
            }
        }
        List<Queue> queueList = new ArrayList<Queue>();
        for (String queue : queues)
        {
            eventQueue = new Queue(queue);
            queueList.add(eventQueue);
        }

        try
        {
            capabilitiesAmqpEntitiesConfig.createAndBindExchange(eventExchange, eventExchangeType, eventRoutingKey, queueList);
        }
        catch (CapabilityBindingException capabilityBindingException)
        {
            throw new EventSubscriptionException(capabilityBindingException);
        }

    }

    private String getEndpointPropertyValue(List<EndpointProperty> endpointProperties, String propertyName)
    {
        EndpointProperty defaultEndpointProperty = new EndpointProperty();
        String endpointPropertyValue = endpointProperties.stream()
                .filter(endpointProperty -> null != endpointProperty.getName() && endpointProperty.getName().equals(propertyName))
                .findFirst().orElse(defaultEndpointProperty).getValue();
        return endpointPropertyValue;
    }
}
