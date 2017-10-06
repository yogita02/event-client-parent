/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * EventServiceImple - This class provides event related api.
 *
 * @see DefaultEventServiceImpl#triggerEvent(String, String, Object)
 */
@Service("eventService")
public class DefaultEventServiceImpl implements EventService
{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void triggerEvent(String eventExchange, String eventRoutingKey, Object event)
    {
        Assert.hasText(eventExchange, "Exchange name cannot be empty");
        Assert.hasText(eventRoutingKey, "Routing key cannot be empty");
        Assert.notNull(event, "Event cannot be empty");
        this.rabbitTemplate.convertAndSend(eventExchange, eventRoutingKey, event);
    }
}
