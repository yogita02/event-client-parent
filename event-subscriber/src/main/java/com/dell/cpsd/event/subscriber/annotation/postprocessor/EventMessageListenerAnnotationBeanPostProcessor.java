/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.event.subscriber.annotation.postprocessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.dell.cpsd.event.client.EventService;
import com.dell.cpsd.event.exceptions.EventSubscriptionException;
import com.dell.cpsd.event.subscriber.annotation.EventMessageHandler;
import com.dell.cpsd.event.subscriber.annotation.EventMessageListener;

/**
 * BeanPostProcessor to scan for @RabbitListener annotation and merge the attributes from @EventMessageListener. This processor also
 * subscribes to the event capability provided and starts listening to the queue for the events
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@Order(1)
public class EventMessageListenerAnnotationBeanPostProcessor extends RabbitListenerAnnotationBeanPostProcessor
        implements ApplicationListener<ContextRefreshedEvent>
{

    @Autowired
    private EventService eventService;

    /**
     * Over-riding the initialization with on application start up event.
     * 
     * @param bean
     *            - {@link Object} After initialization of each bean, this method is invoked by passing the initialized bean object
     * @param beanName
     *            - {@link String} Name of the passed bean object
     * 
     * @return bean - {@link Object} - returns the bean
     */
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
    {
        // Over-riding the initialization on application start up event.
        return bean;
    }

    /**
     * On startup of application, find the bean(s) annotated with @EventMessageListener and registers the bean as rabbitListener. Also,
     * subscribe to the events and register the listeners to listen to the event messages
     * 
     * @param ContextRefreshedEvent
     *            - {@link ContextRefreshedEvent}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        ApplicationContext applicationContext = event.getApplicationContext();

        // Get names of all beans that are created
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();

        ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();

        for (String beanName : allBeanNames)
        {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            if (beanDefinition != null && beanDefinition.isSingleton())
            {
                // Get bean objects from bean names
                Object bean = configurableListableBeanFactory.getSingleton(beanName);
                if (null != bean)
                {
                    Class<?> targetClass = AopUtils.getTargetClass(bean);
                    EventMessageListener eventMessageListener = AnnotationUtils.findAnnotation(targetClass, EventMessageListener.class);
                    // eventMessageListener will be null if class has annotation @RabbitListener
                    // eventMessageListener will be null if class doesn't have any annotation
                    if (null == eventMessageListener)
                    {
                        // If class or method has @RabbitListener then call super class method to process the bean else
                        // call method to process event listener messages
                        if (isEventCapabilityProcessedAtMethodLevel(targetClass))
                        {
                            processEventMessageListener(beanName, bean, targetClass);
                        }
                        else
                        {
                            super.postProcessAfterInitialization(bean, beanName);
                        }
                    }
                    // this else block handles the classes which has @EventMessageListener at class level and @EventMessageHandler at method
                    // level
                    else
                    {
                        List<EventMessageHandler> eventMessageHandlers = findHandlerAnnotations(targetClass);
                        try
                        {
                            subscribeToEventCapability(eventMessageListener, eventMessageHandlers);
                        }
                        catch (EventSubscriptionException eventSubscriptionException)
                        {
                            throw new RuntimeException(eventSubscriptionException);
                        }
                        processEventMessageListener(beanName, bean, targetClass);
                    }
                }

            }
        }
    }

    /**
     * Method to check whether event capability is processed for @EventMessageListener annotation at method level.
     * 
     * @param targetClass
     *            - class that is being scanned for @EventMessageListener
     * @return whether @EventMessageListener is used at method level
     * @throws EventSubscriptionException
     *             when the queue could not be bound to the event exchange
     */
    private boolean isEventCapabilityProcessedAtMethodLevel(Class<?> targetClass)
    {
        boolean isEventCapabilityProcessed = false;
        Method[] methods = targetClass.getMethods();
        for (Method method : methods)
        {
            EventMessageListener eventMessageListener = method.getAnnotation(EventMessageListener.class);
            if (null != eventMessageListener)
            {
                try
                {
                    subscribeToEventCapability(eventMessageListener, null);
                }
                catch (EventSubscriptionException eventSubscriptionException)
                {
                    throw new RuntimeException(eventSubscriptionException);
                }
                isEventCapabilityProcessed = true;
            }
        }
        return isEventCapabilityProcessed;
    }

    private void processEventMessageListener(String beanName, Object bean, Class<?> targetClass)
    {
        Set<RabbitListener> classLevelListeners = findListenerAnnotations(targetClass);
        final List<Method> multiMethods = new ArrayList<Method>();
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback()
        {
            @Override
            public void doWith(Method method) throws IllegalAccessException
            {
                for (RabbitListener rabbitListener : findListenerAnnotations(method))
                {
                    processAmqpListener(rabbitListener, method, bean, beanName);
                }

                if (!CollectionUtils.isEmpty(classLevelListeners))
                {
                    RabbitHandler rabbitHandler = AnnotationUtils.findAnnotation(method, RabbitHandler.class);
                    if (rabbitHandler != null)
                    {
                        multiMethods.add(method);
                    }
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
        if (!CollectionUtils.isEmpty(classLevelListeners))
        {
            processMultiMethodListeners(classLevelListeners, multiMethods, bean, beanName);
        }
    }

    private void subscribeToEventCapability(EventMessageListener eventMessageListener, List<EventMessageHandler> eventMessageHandlers)
            throws EventSubscriptionException
    {
        if (null != eventMessageListener)
        {
            List<String> queues = Arrays.asList(eventMessageListener.queues());
            Assert.isTrue(!CollectionUtils.isEmpty(queues), "List of queues cannot be null/empty");
            String queueWithoutName = queues.stream().filter(queue -> !StringUtils.hasText(queue)).findFirst().orElse(null);
            Assert.isNull(queueWithoutName, "Queue Name cannot be null/empty.");
            if (!CollectionUtils.isEmpty(eventMessageHandlers))
            {
                for (EventMessageHandler eventMessageHandler : eventMessageHandlers)
                {
                    Assert.hasText(eventMessageHandler.capability(), "Capability Name cannot be null/empty.");
                    eventService.subscribeToEvent(queues, eventMessageHandler.capability());
                }
            }
            else
            {
                Assert.hasText(eventMessageListener.capability(), "Capability Name cannot be null/empty.");
                eventService.subscribeToEvent(queues, eventMessageListener.capability());
            }
        }
    }

    private List<EventMessageHandler> findHandlerAnnotations(Class<?> targetClass)
    {
        Method[] methods = targetClass.getMethods();
        List<EventMessageHandler> listeners = new ArrayList<EventMessageHandler>();
        for (Method method : methods)
        {
            EventMessageHandler eventMessageHandler = method.getAnnotation(EventMessageHandler.class);
            if (null != eventMessageHandler)
            {
                listeners.add(eventMessageHandler);
            }
        }
        return listeners;
    }

    private Set<RabbitListener> findListenerAnnotations(Class<?> clazz)
    {
        Set<RabbitListener> listeners = new HashSet<RabbitListener>();
        RabbitListener annotation = AnnotatedElementUtils.findMergedAnnotation(clazz, RabbitListener.class);
        if (annotation != null)
        {
            listeners.add(annotation);
        }
        return listeners;
    }

    private Set<RabbitListener> findListenerAnnotations(Method method)
    {
        Set<RabbitListener> listeners = new HashSet<RabbitListener>();
        RabbitListener annotation = AnnotatedElementUtils.findMergedAnnotation(method, RabbitListener.class);
        if (annotation != null)
        {
            listeners.add(annotation);
        }
        return listeners;
    }

    private void processMultiMethodListeners(Collection<RabbitListener> classLevelListeners, List<Method> multiMethods, Object bean,
            String beanName)
    {
        List<Method> checkedMethods = new ArrayList<Method>();
        for (Method method : multiMethods)
        {
            checkedMethods.add(checkProxy(method, bean));
        }
        for (RabbitListener classLevelListener : classLevelListeners)
        {
            MultiMethodRabbitListenerEndpoint endpoint = new MultiMethodRabbitListenerEndpoint(checkedMethods, bean);
            processListener(endpoint, classLevelListener, bean, bean.getClass(), beanName);
        }
    }

    private Method checkProxy(Method method, Object bean)
    {
        if (AopUtils.isJdkDynamicProxy(bean))
        {
            try
            {
                // Found a @RabbitListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces)
                {
                    try
                    {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    }
                    catch (NoSuchMethodException noMethod)
                    {
                        throw new RuntimeException(noMethod);
                    }
                }
            }
            catch (SecurityException ex)
            {
                ReflectionUtils.handleReflectionException(ex);
            }
            catch (NoSuchMethodException ex)
            {
                throw new IllegalStateException(String.format(
                        "@RabbitListener method '%s' found on bean target class '%s', "
                                + "but not found in any interface(s) for bean JDK proxy. Either "
                                + "pull the method up to an interface or switch to subclass (CGLIB) "
                                + "proxies by setting proxy-target-class/proxyTargetClass " + "attribute to 'true'",
                        method.getName(), method.getDeclaringClass().getSimpleName()));
            }
        }
        return method;
    }

}
