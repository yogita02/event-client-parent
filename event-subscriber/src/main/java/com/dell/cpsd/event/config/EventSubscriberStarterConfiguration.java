/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import com.dell.cpsd.hdp.capability.registry.client.binding.config.CapabilityRegistryBindingManagerConfig;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryLookupManagerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Event Subscriber Starter Initial configuration
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
// Exclude test classes in component scan
@Configuration
@ComponentScan(basePackages = {
        "com.dell.cpsd.event.client"}, excludeFilters = {
        @Filter(type = FilterType.REGEX, pattern = "com.dell.cpsd.event.*.*Test.*"),
        @Filter(type = FilterType.REGEX, pattern = "com.dell.cpsd.event.*.*IT.*")})
public class EventSubscriberStarterConfiguration
{
    /**
     * create bean for ObjectMapper
     * 
     * @return {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }
}
