/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import com.dell.cpsd.hdp.capability.registry.capability.annotations.Capability;
import com.dell.cpsd.hdp.capability.registry.capability.annotations.CapabilityProvider;
import com.dell.cpsd.hdp.capability.registry.constants.AmqpExchangeTypes;
import com.dell.cpsd.hdp.capability.registry.dto.CapabilityData;
import com.dell.cpsd.hdp.capability.registry.dto.RequestQueue;

/**
 * Capability provider class where executable and event capabilities are configured
 * 
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 * Capability provider class where all the Capabilities are defined.
 *
 */
@CapabilityProvider
@SuppressWarnings("deprecation")
public class SampleCapabilityProvider
{

    /**
     * Provides event capability data for Storage space running out event
     * 
     * @return{@link CapabilityData}
     */
    @Capability(name = CapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_PROFILE_NAME, isEvent = true)
    public CapabilityData storageSpaceRunningOutEventCapability()
    {
        CapabilityData capabilityData = new CapabilityData();
        capabilityData.setEventExchange(CapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_EXCHANGE);
        capabilityData.setEventExchangeType(AmqpExchangeTypes.TOPIC);
        capabilityData.setEventMessageType(CapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_MESSAGE_TYPE);
        capabilityData.setEventMessageVersion(CapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_MESSAGE_VERSION);
        capabilityData.setEventRoutingKey(CapabilityConfiguration.STORAGE_SPACE_RUNNING_OUT_EVENT_ROUTING_KEY);
        return capabilityData;
    }

    /**
     * Return capability data object with required capability details populated for Capability 'add-storage'
     * 
     * @return CapabilityData {@link CapabilityData}
     */
    @Capability
    public CapabilityData addStorage()
    {
        CapabilityData capabilityData = new CapabilityData();
        capabilityData.setType(CapabilityConfiguration.ADD_STORAGE_ENDPOINT_TYPE);
        capabilityData.setProfileName(CapabilityConfiguration.ADD_STORAGE_PROFILE_NAME);

        RequestQueue requestQueue = new RequestQueue();
        requestQueue.setName(CapabilityConfiguration.ADD_STORAGE_REQUEST_QUEUE);
        capabilityData.setRequestQueue(requestQueue);

        capabilityData.setRequestExchange(CapabilityConfiguration.ADD_STORAGE_REQUEST_EXCHANGE);
        capabilityData.setRequestExchangeType(AmqpExchangeTypes.TOPIC);
        capabilityData.setRequestRoutingKey(CapabilityConfiguration.ADD_STORAGE_REQUEST_ROUTING_KEY);
        capabilityData.setRequestMessageType(CapabilityConfiguration.ADD_STORAGE_REQUEST_MESSAGE_TYPE);

        capabilityData.setResponseExchange(CapabilityConfiguration.ADD_STORAGE_RESPONSE_EXCHANGE);
        capabilityData.setResponseExchangeType(AmqpExchangeTypes.TOPIC);
        capabilityData.setResponseRoutingKey(CapabilityConfiguration.ADD_STORAGE_RESPONSE_ROUTING_KEY);
        capabilityData.setResponseMessageType(CapabilityConfiguration.ADD_STORAGE_RESPONSE_MESSAGE_TYPE);

        return capabilityData;
    }

    /**
     * Provides event capability data for Storage space running out event
     * 
     * @return{@link CapabilityData}
     */
    @Capability(name = CapabilityConfiguration.SAMPLE_EVENT_PROFILE_NAME, isEvent = true)
    public CapabilityData sampleEventCapability()
    {
        CapabilityData capabilityData = new CapabilityData();
        capabilityData.setEventExchange(CapabilityConfiguration.SAMPLE_EVENT_EXCHANGE);
        capabilityData.setEventExchangeType(AmqpExchangeTypes.TOPIC);
        capabilityData.setEventMessageType(CapabilityConfiguration.SAMPLE_EVENT_MESSAGE_TYPE);
        capabilityData.setEventMessageVersion(CapabilityConfiguration.SAMPLE_EVENT_MESSAGE_VERSION);
        capabilityData.setEventRoutingKey(CapabilityConfiguration.SAMPLE_EVENT_ROUTING_KEY);
        return capabilityData;
    }
}
