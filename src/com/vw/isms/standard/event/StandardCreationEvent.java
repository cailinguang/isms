package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;
import com.vw.isms.standard.model.StandardType;

public class StandardCreationEvent
  implements StandardEvent
{
  private final StandardType standardType;
  
  public StandardCreationEvent(StandardType standardType)
  {
    this.standardType = standardType;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardCreation(this);
  }
  
  public StandardType getStandardType()
  {
    return this.standardType;
  }
}
