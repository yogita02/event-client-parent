/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.subscriber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;

/**
 * Annotation to handle the events which are subscribed.If this annotation is used at method levelthen the class needs to be annotated
 * with @EventMessageListener
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RabbitHandler
public @interface EventMessageHandler
{
    /**
     * Profile name of the event that has to be subscribed
     */
    String capability() default "";
}
