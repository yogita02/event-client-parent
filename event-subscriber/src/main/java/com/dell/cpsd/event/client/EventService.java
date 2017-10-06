/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import java.util.List;

import com.dell.cpsd.event.exceptions.EventSubscriptionException;

public interface EventService
{
    /**
     * Method to trigger an event of any type.
     * 
     * @param eventExchange
     *            - an event capability exchange
     * @param eventRoutingKey
     *            - an event capability routing key
     * @param event
     *            - an event to be triggered
     */
    void triggerEvent(String eventExchange, String eventRoutingKey, Object event);

    /**
     * Method to subscribe to an event capability
     * 
     * @param queues
     *            - queues to which the event exchange needs to be bound
     * @param eventCapability
     *            - event capability to be subscribed
     * @throws EventSubscriptionException
     *             when event capability is not available in the capability registry or if the queue could be bound to the event exchange
     */
    void subscribeToEvent(List<String> queues, String eventCapability) throws EventSubscriptionException;
}
