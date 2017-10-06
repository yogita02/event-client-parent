/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.subscriber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.annotation.AliasFor;

import com.dell.cpsd.common.rabbitmq.config.ConsumerConfig;

/**
 * Annotation to subscribe to an event.This can be used at a class level as well as method level.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RabbitListener
public @interface EventMessageListener
{

    /**
     * The unique identifier of the container managing for this endpoint.
     * <p>
     * If none is specified an auto-generated one is provided.
     * 
     * @return the {@code id} for the container managing for this endpoint.
     * @see org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry#getListenerContainer(String)
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "id")
    String id() default "";

    /**
     * The bean name of the {@link org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory} to use to create the message
     * listener container responsible to serve this endpoint.
     * <p>
     * If not specified, the default container factory is used, if any. Default value for container factory is
     * simpleRabbitListenerContainerFactory. This is initialized in {@link ConsumerConfig}
     * 
     * @return the {@link org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory} bean name.
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "containerFactory")
    String containerFactory() default "simpleRabbitListenerContainerFactory";

    /**
     * The queues for this listener. The entries can be 'queue name', 'property-placeholder keys' or 'expressions'. Expression must be
     * resolved to the queue name or {@code Queue} object. Mutually exclusive with {@link #bindings()}
     * 
     * @return the queue names or expressions (SpEL) to listen to from target
     *         {@link org.springframework.amqp.rabbit.listener.MessageListenerContainer}.
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "queues")
    String[] queues() default {};

    /**
     * When {@code true}, a single consumer in the container will have exclusive use of the {@link #queues()}, preventing other consumers
     * from receiving messages from the queues. When {@code true}, requires a concurrency of 1. Default {@code false}.
     * 
     * @return the {@code exclusive} boolean flag.
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "exclusive")
    boolean exclusive() default false;

    /**
     * The priority of this endpoint. Requires RabbitMQ 3.2 or higher. Does not change the container priority by default. Larger numbers
     * indicate higher priority, and both positive and negative numbers can be used.
     * 
     * @return the priority for the endpoint.
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "priority")
    String priority() default "";

    /**
     * Reference to a {@link org.springframework.amqp.rabbit.core.RabbitAdmin RabbitAdmin}. Required if the listener is using auto-delete
     * queues and those queues are configured for conditional declaration. This is the admin that will (re)declare those queues when the
     * container is (re)started. See the reference documentation for more information.
     * 
     * @return the {@link org.springframework.amqp.rabbit.core.RabbitAdmin} bean name.
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "admin")
    String admin() default "";

    /**
     * Array of {@link QueueBinding}s providing the listener's queue names, together with the exchange and optional binding information.
     * 
     * @return the bindings.
     * @since 1.5
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "bindings")
    QueueBinding[] bindings() default {};

    /**
     * If provided, the listener container for this listener will be added to a bean with this value as its name, of type
     * {@code Collection<MessageListenerContainer>}. This allows, for example, iteration over the collection to start/stop a subset of
     * containers.
     * 
     * @return the bean name for the group.
     * @since 1.5
     */
    @AliasFor(annotation = RabbitListener.class, attribute = "group")
    String group() default "";

    String capability() default "";
}
