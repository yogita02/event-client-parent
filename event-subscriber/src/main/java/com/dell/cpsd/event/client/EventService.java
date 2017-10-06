/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

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
}
