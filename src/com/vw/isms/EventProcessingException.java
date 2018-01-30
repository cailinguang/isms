package com.vw.isms;

public class EventProcessingException
        extends Exception
{
    private static final long serialVersionUID = 1016267425689695723L;

    public EventProcessingException(String msg)
    {
        super(msg);
    }

    public EventProcessingException(Throwable cause)
    {
        super(cause);
    }

    public EventProcessingException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
