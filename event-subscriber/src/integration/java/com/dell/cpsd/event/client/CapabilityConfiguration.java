/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import com.dell.cpsd.common.rabbitmq.constants.MessagingProtocols;

/**
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 * Constants class where the AMQP properties of all the Capabilities are defined
 *
 */
public class CapabilityConfiguration
{
    // storage space running out event capability endpoint properties

    public static final String STORAGE_SPACE_RUNNING_OUT_EVENT_PROFILE_NAME    = "storage-space-running-out";
    public static final String STORAGE_SPACE_RUNNING_OUT_EVENT_EXCHANGE        = "exchange.dell.cpsd.storage.space.running.out";
    public static final String STORAGE_SPACE_RUNNING_OUT_EVENT_ROUTING_KEY     = "com.dell.cpsd.storage.space.running.out.event.routing.key";
    public static final String STORAGE_SPACE_RUNNING_OUT_EVENT_MESSAGE_TYPE    = "com.dell.cpsd.storage.space.running.out.event";
    public static final String STORAGE_SPACE_RUNNING_OUT_EVENT_MESSAGE_VERSION = "com.dell.cpsd.storage.space.running.out.event.version";

    // List storage capability endpoint properties
    public static final String ADD_STORAGE_ENDPOINT_TYPE                       = MessagingProtocols.AMQP.value();
    public static final String ADD_STORAGE_PROFILE_NAME                        = "sample-add-storage";
    public static final String ADD_STORAGE_REQUEST_QUEUE                       = "queue.dell.cpsd.sample.queue.addstorage";

    public static final String ADD_STORAGE_REQUEST_EXCHANGE                    = "exchange.dell.cpsd.sample.add.storage";
    public static final String ADD_STORAGE_REQUEST_ROUTING_KEY                 = "dell.cpsd.sample.add.storage";
    public static final String ADD_STORAGE_REQUEST_MESSAGE_TYPE                = "com.dell.cpsd.sample.add.storage.request";

    public static final String ADD_STORAGE_RESPONSE_EXCHANGE                   = "exchange.dell.cpsd.integration.response";
    public static final String ADD_STORAGE_RESPONSE_ROUTING_KEY                = "dell.cpsd.sample.add.storage.response{replyTo}";
    public static final String ADD_STORAGE_RESPONSE_MESSAGE_TYPE               = "com.dell.cpsd.sample.add.storage.response";

    // storage space running out event capability endpoint properties

    public static final String SAMPLE_EVENT_PROFILE_NAME                       = "sample-storage-space-running-out";
    public static final String SAMPLE_EVENT_EXCHANGE                           = "exchange.dell.cpsd.sample.event";
    public static final String SAMPLE_EVENT_ROUTING_KEY                        = "com.dell.cpsd.sample.event.routing.key";
    public static final String SAMPLE_EVENT_MESSAGE_TYPE                       = "com.dell.cpsd.sample.event";
    public static final String SAMPLE_EVENT_MESSAGE_VERSION                    = "com.dell.cpsd.sample.event.version";

}
