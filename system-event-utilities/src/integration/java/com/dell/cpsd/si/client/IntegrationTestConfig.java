/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.si.client;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.dell.cpsd.hdp.capability.registry.client.CapabilityRegistryException;
import com.dell.cpsd.si.config.SystemIntegrationStarterConfiguration;
import com.dell.cpsd.si.consumer.listener.annotation.EnableCapabilityRegistration;

/**
 * The class contains all the related configurations required to run integration tests
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Configuration
@ComponentScan(basePackages = {"com.dell.cpsd.hdp.capability.registry.client", "com.dell.cpsd.si.config", "com.dell.cpsd.si.amqp.config",
        "com.dell.cpsd.si.service", "com.dell.cpsd.event.client"})
@PropertySources({@PropertySource("classpath:rabbitmq.properties"), @PropertySource(value = "classpath:persistence.properties")})
@Import({SystemIntegrationStarterConfiguration.class, CapabilityRegistryException.class})
@EnableCapabilityRegistration
public class IntegrationTestConfig
{

    /**
     * This will create Queue for annotation integration testing
     * 
     * @return
     */
    @Bean
    public Queue queueToTestMessageListenerAnnotation()
    {
        return new Queue("queue.dell.cpsd.sample.queue.annotation.it");
    }
}
