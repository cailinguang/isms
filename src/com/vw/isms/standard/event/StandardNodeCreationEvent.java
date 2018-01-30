package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;
import com.vw.isms.web.StandardNodeRequest;

public class StandardNodeCreationEvent
  implements StandardEvent
{
  private final long standardId;
  private final StandardNodeRequest request;
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public StandardNodeCreationEvent(long standardId, StandardNodeRequest request)
  {
    this.standardId = standardId;
    this.request = request;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardNodeCreation(this);
  }
  
  public StandardNodeRequest getRequest()
  {
    return this.request;
  }
}
