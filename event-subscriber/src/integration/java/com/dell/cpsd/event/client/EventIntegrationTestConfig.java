/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.client;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.dell.cpsd.common.rabbitmq.config.ConsumerConfig;
import com.dell.cpsd.common.rabbitmq.config.IRabbitMqPropertiesConfig;
import com.dell.cpsd.common.rabbitmq.config.PropertiesConfig;
import com.dell.cpsd.common.rabbitmq.config.RabbitConfig;
import com.dell.cpsd.common.rabbitmq.config.RabbitMqProductionConfig;
import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQCachingConnectionFactory;
import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQTLSFactoryBean;
import com.dell.cpsd.common.rabbitmq.registration.notifier.config.RegistrationConfig;
import com.dell.cpsd.event.config.EventMessageListenerBootstrapConfiguration;
import com.dell.cpsd.hdp.capability.registry.amqp.config.ContextConfig;
import com.dell.cpsd.hdp.capability.registry.capability.annotations.EnableCapabilityRegistration;

/**
 * The class contains all the related configurations required to run integration tests
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Configuration
@ComponentScan(basePackages = {
        "com.dell.cpsd.event.client", "com.dell.cpsd.event.subscriber.annotation", "com.dell.cpsd.hdp.capability.registry"})
@PropertySources({@PropertySource("classpath:rabbitmq.properties"), @PropertySource(value = "classpath:persistence.properties")})
@Import({RabbitConfig.class, PropertiesConfig.class, ConsumerConfig.class, RabbitMqProductionConfig.class, RegistrationConfig.class,
        ContextConfig.class, EventMessageListenerBootstrapConfiguration.class})
@EnableCapabilityRegistration
public class EventIntegrationTestConfig
{
    /*
     * The configuration properties for the client.
     */
    @Autowired
    @Qualifier("rabbitPropertiesConfig")
    private IRabbitMqPropertiesConfig propertiesConfig;

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

    /**
     * @return The <code>ConnectionFactory</code> to use.
     * @since SINCE-TBD TODO: Reuse the config from RabbitMqProductionConfig. Currently that bean is set to production profile and hence
     *        cannot be used.
     */
    @Bean
    @Qualifier("rabbitConnectionFactory")
    public ConnectionFactory productionCachingConnectionFactory()
    {
        RabbitMQCachingConnectionFactory cachingCF = null;
        com.rabbitmq.client.ConnectionFactory connectionFactory;

        try
        {
            if (propertiesConfig.isSslEnabled())
            {
                RabbitMQTLSFactoryBean rabbitMQTLSFactoryBean = new RabbitMQTLSFactoryBean(propertiesConfig);
                connectionFactory = rabbitMQTLSFactoryBean.getObject();
            }
            else
            {
                connectionFactory = new com.rabbitmq.client.ConnectionFactory();
            }

            cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return cachingCF;
    }
}
