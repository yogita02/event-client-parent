/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.event.config;

import org.springframework.amqp.rabbit.annotation.RabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import com.dell.cpsd.event.subscriber.annotation.postprocessor.EventMessageListenerAnnotationBeanPostProcessor;

/**
 * Configuration to override the {@link RabbitBootstrapConfiguration}. This class is used to create bean of
 * EventMessageListenerAnnotationBeanPostProcessor instead of RabbitListenerAnnotationBeanPostProcessor
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p>
 * 
 *
 */
public class EventMessageListenerBootstrapConfiguration extends RabbitBootstrapConfiguration
{
    /**
     * Creates a bean of EventMessageListenerAnnotationBeanPostProcessor instead of RabbitListenerAnnotationBeanPostProcessorfor bean id
     * {@link RabbitListenerConfigUtils#RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME}. This is used to enable meta annotation for
     * {@link RabbitListener} and merge the aliasFor properties
     */
    @Bean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Override
    public RabbitListenerAnnotationBeanPostProcessor rabbitListenerAnnotationProcessor()
    {
        return new EventMessageListenerAnnotationBeanPostProcessor();
    }
}
