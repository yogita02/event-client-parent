/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.event.subscriber.annotation.postprocessor;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.dell.cpsd.event.client.EventService;
import com.dell.cpsd.event.exceptions.EventSubscriptionException;
import com.dell.cpsd.event.subscriber.annotation.EventMessageHandler;
import com.dell.cpsd.event.subscriber.annotation.EventMessageListener;
import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityService;
import com.dell.cpsd.hdp.capability.registry.exceptions.CapabilityRetrievalException;

/**
 * Test class for EventMessageListenerAnnotationBeanPostProcessor
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
@RunWith(MockitoJUnitRunner.class)
public class EventMessageListenerAnnotationBeanPostProcessorTest
{
    @InjectMocks
    EventMessageListenerAnnotationBeanPostProcessor classUnderTest;

    @Mock
    ApplicationContext                              applicationContext;

    @Mock
    AbstractApplicationContext                      abstractApplicationContext;

    @Mock
    ConfigurableListableBeanFactory                 configurableListableBeanFactory;

    @Mock
    RabbitListenerEndpointRegistrar                 rabbitListenerEndpointRegistrar;

    @Mock
    ConfigurableApplicationContext                  configurableApplicationContext;

    @Mock
    ContextRefreshedEvent                           contextRefreshedEvent;

    @Mock
    MethodRabbitListenerEndpoint                    methodRabbitListenerEndpoint;

    @Mock
    ICapabilityService                               capabilityService;

    @Mock
    AmqpAdmin                                       rabbitAdmin;

    @Mock
    BeanFactory                                     beanFactoryMock;

    @Mock
    BeanDefinition                                  beanDefinitionMock;

    @Captor
    ArgumentCaptor<List<String>>                    queueCaptor;

    @Captor
    ArgumentCaptor<String>                          capabilityNameCaptor;
    @Mock
    EventService                                    eventServiceMock;

    Capability                                      capability;

    /**
     * Sets the up the data required before each test case is run.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        capability = createCapability();
    }

    /**
     * Invoked after each test case has been run.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        classUnderTest = null;
    }

    /**
     * Positive - Verify that queue and event exchange binding happens successfully
     * 
     * @throws CapabilityRetrievalException
     * @throws EventSubscriptionException
     */
    @Test
    public void testOnApplicationEventVerifyBinding() throws CapabilityRetrievalException, EventSubscriptionException
    {
        TestEventListener bean = new TestEventListener();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        classUnderTest = new EventMessageListenerAnnotationBeanPostProcessor()
        {
            @Override
            protected void processListener(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object passedBean,
                    Object adminTarget, String beanName)
            {

                // Overriding the method to test. Actual method will directly call super class implementation. Assert here if the expected
                // values are passed on from Bean Processor.

                assertEquals(passedBean, bean);
                assertEquals(rabbitListener.queues().length, 1);
                assertEquals(rabbitListener.containerFactory(), "simpleRabbitListenerContainerFactory");
                assertEquals(rabbitListener.queues()[0], "test.queue");
                assertEquals(beanName, "simpleMessageListner");
            }
        };
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
        Mockito.verify(beanDefinitionMock).isSingleton();
        Mockito.verify(eventServiceMock).subscribeToEvent(queueCaptor.capture(), capabilityNameCaptor.capture());
        assertEquals("test.queue", queueCaptor.getValue().get(0));
        assertEquals("cpu-utilization", capabilityNameCaptor.getValue());
    }

    /**
     * Negative - Verify that IllegalArgumentException is thrown when capability name is not provided
     * 
     * @throws CapabilityRetrievalException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnApplicationEventWithoutCapabilityName() throws CapabilityRetrievalException
    {
        TestEventListenerWithoutCapability bean = new TestEventListenerWithoutCapability();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);

        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - Verify that IllegalArgumentException is thrown when queue is not available
     * 
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnApplicationEventWithoutQueueName() throws CapabilityRetrievalException
    {
        TestEventListenerWithoutQueue bean = new TestEventListenerWithoutQueue();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);

        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Positive - Verify that queue and event exchange binding happens successfully for two queues
     * 
     * @throws CapabilityRetrievalException
     * @throws EventSubscriptionException
     */
    @Test
    public void testOnApplicationEventVerifyBindingWithTwoQueue() throws CapabilityRetrievalException, EventSubscriptionException
    {
        TestEventListenerWithoutTwoQueues bean = new TestEventListenerWithoutTwoQueues();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        classUnderTest = new EventMessageListenerAnnotationBeanPostProcessor()
        {
            @Override
            protected void processListener(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object passedBean,
                    Object adminTarget, String beanName)
            {

                // Overriding the method to test. Actual method will directly call super class implementation. Assert here if the expected
                // values are passed on from Bean Processor.

                assertEquals(passedBean, bean);
                assertEquals(rabbitListener.queues().length, 2);
                assertEquals(rabbitListener.containerFactory(), "simpleRabbitListenerContainerFactory");
                assertEquals(rabbitListener.queues()[0], "test.queue");
                assertEquals(rabbitListener.queues()[1], "sample.queue");
                assertEquals(beanName, "simpleMessageListner");
            }
        };
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
        Mockito.verify(beanDefinitionMock).isSingleton();
        Mockito.verify(eventServiceMock).subscribeToEvent(queueCaptor.capture(), capabilityNameCaptor.capture());
        assertEquals("test.queue", queueCaptor.getValue().get(0));
        assertEquals("sample.queue", queueCaptor.getValue().get(1));
        assertEquals("cpu-utilization", capabilityNameCaptor.getValue());
    }

    /**
     * Negative - Verify that binding does not happen when the bean definition is null
     * 
     * @throws CapabilityRetrievalException
     */
    @Test
    public void testOnApplicationEventWithBeanDefinitionNull() throws CapabilityRetrievalException
    {
        TestEventListener bean = new TestEventListener();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(null);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);

        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - Verify that binding does not happen when the bean is not singleton
     * 
     * @throws CapabilityRetrievalException
     */
    @Test
    public void testOnApplicationEventWithNonSingletonBean() throws CapabilityRetrievalException
    {
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(false);

        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - Verify that exception is thrown when queue name is not provided
     * 
     * @throws CapabilityRetrievalException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnApplicationEventWithNoQueue() throws CapabilityRetrievalException
    {
        TestEventListenerWithoutQueue bean = new TestEventListenerWithoutQueue();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);

        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Positive - Verify that queue and event exchange binding happens successfully when EventMessageListener annotation is placed at method
     * level
     * 
     * @throws CapabilityRetrievalException
     * @throws EventSubscriptionException
     */
    @Test
    public void testOnApplicationEventWithMethodLevelListener() throws CapabilityRetrievalException, EventSubscriptionException
    {
        TestEventListenerWithMethodLevelListenerClass1 bean = new TestEventListenerWithMethodLevelListenerClass1();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        classUnderTest = new EventMessageListenerAnnotationBeanPostProcessor()
        {
            @Override
            protected void processAmqpListener(RabbitListener rabbitListener, Method method, Object passedBean, String beanName)
            {

                // Overriding the method to test. Actual method will directly call super class implementation. Assert here if the expected
                // values are passed on from Bean Processor.

                assertEquals(passedBean, bean);
                assertEquals(rabbitListener.queues().length, 1);
                assertEquals(rabbitListener.containerFactory(), "simpleRabbitListenerContainerFactory");
                assertEquals(rabbitListener.queues()[0], "test.queue");
                assertEquals(method.getName(), "testHandler");
                assertEquals(beanName, "simpleMessageListner");
            }

        };
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
        Mockito.verify(beanDefinitionMock).isSingleton();
        Mockito.verify(eventServiceMock).subscribeToEvent(queueCaptor.capture(), capabilityNameCaptor.capture());
        assertEquals("test.queue", queueCaptor.getValue().get(0));
        assertEquals("cpu-utilization", capabilityNameCaptor.getValue());
    }

    /**
     * Negative - Verify that IllegalArgumentException is thrown when EventMessageListener annotation is used at method level and capability
     * name is not provided
     * 
     * @throws CapabilityRetrievalException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnApplicationEventWithNoCapabilityName() throws CapabilityRetrievalException
    {
        TestEventListenerWithMethodLevelListenerClass2 bean = new TestEventListenerWithMethodLevelListenerClass2();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - Verify that IllegalArgumentException is thrown when EventMessageListener annotation is used at method level and capability
     * name is not provided
     * 
     * @throws CapabilityRetrievalException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testOnApplicationEventWithNoQueues() throws CapabilityRetrievalException
    {
        TestEventListenerWithMethodLevelListenerClass3 bean = new TestEventListenerWithMethodLevelListenerClass3();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - Verify that RuntimeException is thrown when Subscription exception is thrown
     * 
     * @throws CapabilityRetrievalException
     * @throws EventSubscriptionException
     */
    @Test(expected = RuntimeException.class)
    public void testOnApplicationEventWithException() throws CapabilityRetrievalException, EventSubscriptionException
    {
        TestEventListener bean = new TestEventListener();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        Mockito.doThrow(EventSubscriptionException.class).when(eventServiceMock).subscribeToEvent(Mockito.any(), Mockito.any());
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    /**
     * Negative - With method level EventMessageListener,Verify that RuntimeException is thrown when Subscription exception is thrown
     * 
     * @throws CapabilityRetrievalException
     * @throws EventSubscriptionException
     */
    @Test(expected = RuntimeException.class)
    public void testOnApplicationEventWithExceptionForMethodLevelListener() throws CapabilityRetrievalException, EventSubscriptionException
    {
        TestEventListenerWithMethodLevelListenerClass1 bean = new TestEventListenerWithMethodLevelListenerClass1();
        String[] beanNames = {"simpleMessageListner"};
        Mockito.when(contextRefreshedEvent.getApplicationContext()).thenReturn(abstractApplicationContext);
        Mockito.when(abstractApplicationContext.getBeanDefinitionNames()).thenReturn(beanNames);
        Mockito.when((abstractApplicationContext).getAutowireCapableBeanFactory()).thenReturn(configurableListableBeanFactory);
        Mockito.when(configurableListableBeanFactory.getBeanDefinition(Mockito.anyString())).thenReturn(beanDefinitionMock);
        Mockito.when(beanDefinitionMock.isSingleton()).thenReturn(true);
        Mockito.when(configurableListableBeanFactory.getSingleton("simpleMessageListner")).thenReturn(bean);
        Mockito.doThrow(EventSubscriptionException.class).when(eventServiceMock).subscribeToEvent(Mockito.any(), Mockito.any());
        ReflectionTestUtils.setField(classUnderTest, "eventService", eventServiceMock);
        classUnderTest.onApplicationEvent(new ContextRefreshedEvent(abstractApplicationContext));
    }

    private Capability createCapability()
    {
        Capability capability = new Capability();
        capability.setProviderEndpoint(createProviderEndpoint());
        return capability;
    }

    private ProviderEndpoint createProviderEndpoint()
    {
        ProviderEndpoint providerEndpoint = new ProviderEndpoint();
        List<EndpointProperty> endpointProperties = new ArrayList<EndpointProperty>();
        EndpointProperty exchangeEndpointProperty = new EndpointProperty("event-exchange", "test-event-exchange");
        EndpointProperty routingKeyEndpointProperty = new EndpointProperty("event-routing-key", "test-event-routing-key");
        EndpointProperty exchangeTypeEndpointProperty = new EndpointProperty("event-exchange-type", ExchangeTypes.TOPIC);
        endpointProperties.add(exchangeEndpointProperty);
        endpointProperties.add(routingKeyEndpointProperty);
        endpointProperties.add(exchangeTypeEndpointProperty);
        providerEndpoint.setEndpointProperties(endpointProperties);
        providerEndpoint.setType(ExchangeTypes.TOPIC);
        return providerEndpoint;
    }

}

@EventMessageListener(queues = "test.queue")
class TestEventListener
{

    @EventMessageHandler(capability = "cpu-utilization")
    public void testHandler()
    {
        // Intentionally blank
    }
}

@EventMessageListener(queues = "test.queue", capability = "")
class TestEventListenerWithoutCapability
{

    @RabbitHandler
    public void testHandler()
    {
        // Intentionally blank
    }
}

@EventMessageListener(queues = "")
class TestEventListenerWithoutQueue
{

    @EventMessageHandler(capability = "cpu-utilization")
    public void testHandler()
    {
        // Intentionally blank
    }
}

@EventMessageListener(queues = {"test.queue", "sample.queue"})
class TestEventListenerWithoutTwoQueues
{

    @EventMessageHandler(capability = "cpu-utilization")
    public void testHandler()
    {
        // Intentionally blank
    }
}

class TestEventListenerWithMethodLevelListenerClass1
{

    @EventMessageListener(queues = {"test.queue"}, capability = "cpu-utilization")
    public void testHandler()
    {
        // Intentionally blank
    }
}

class TestEventListenerWithMethodLevelListenerClass2
{

    @EventMessageListener(queues = {"test.queue"})
    public void testHandler()
    {
        // Intentionally blank
    }
}

class TestEventListenerWithMethodLevelListenerClass3
{

    @EventMessageListener(capability = "cpu-utilization")
    public void testHandler()
    {
        // Intentionally blank
    }
}

@EventMessageListener(queues = {"test.queue"})
class TestEventListenerWithEventHandlerClass1
{

    public void testHandler()
    {
        // Intentionally blank
    }
}

@RabbitListener(queues = "test.queue", containerFactory = "simpleRabbitListenerContainerFactory")
class TestMessageListener
{

    @RabbitHandler
    public void testHandler()
    {
        // Intentionally blank
    }
}
