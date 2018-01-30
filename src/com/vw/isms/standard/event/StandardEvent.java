package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;

public abstract interface StandardEvent
{
  public abstract void process(StandardEventProcessor paramStandardEventProcessor)
    throws EventProcessingException;
}
