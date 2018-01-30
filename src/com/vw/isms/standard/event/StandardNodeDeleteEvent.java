package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;

public class StandardNodeDeleteEvent
  implements StandardEvent
{
  private final long standardId;
  private final long nodeId;
  
  public StandardNodeDeleteEvent(long standardId, long nodeId)
  {
    this.standardId = standardId;
    this.nodeId = nodeId;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardNodeDeletion(this);
  }
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public long getNodeId()
  {
    return this.nodeId;
  }
}
