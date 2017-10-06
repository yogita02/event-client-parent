/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.event.exceptions;

/**
 * EventSubscriptionException - Custom checked exception class for event subscription
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
public class EventSubscriptionException extends Exception
{

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new EventSubscriptionException.
     *
     * @param message
     *            {@link String} - encapsulated in EventSubscriptionException object
     */
    public EventSubscriptionException(String message)
    {
        super(message);
    }

    /**
     * Instantiates a new EventSubscriptionException.
     *
     * @param message
     *            {@link String} - encapsulated in EventSubscriptionException object
     * @param cause
     *            {@link Throwable} - throwable object that caused the exception.
     */
    public EventSubscriptionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Instantiates a new EventSubscriptionException.
     *
     * @param cause
     *            {@link Throwable} - throwable object that caused the exception.
     */
    public EventSubscriptionException(Throwable cause)
    {
        super(cause);
    }

}
