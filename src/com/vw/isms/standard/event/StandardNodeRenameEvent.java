package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;

public class StandardNodeRenameEvent
  implements StandardEvent
{
  private final long standardId;
  private final long nodeId;
  private final String newName;
  
  public StandardNodeRenameEvent(long standardId, long nodeId, String newName)
  {
    this.standardId = standardId;
    this.nodeId = nodeId;
    this.newName = newName;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardNodeRename(this);
  }
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public long getNodeId()
  {
    return this.nodeId;
  }
  
  public String getNewName()
  {
    return this.newName;
  }
}
