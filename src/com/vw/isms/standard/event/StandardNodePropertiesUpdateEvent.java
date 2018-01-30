package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;
import com.vw.isms.property.Properties;

public class StandardNodePropertiesUpdateEvent
  implements StandardEvent
{
  private long standardId;
  private long nodeId;
  private Properties properties;
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public void setStandardId(long standardId)
  {
    this.standardId = standardId;
  }
  
  public long getNodeId()
  {
    return this.nodeId;
  }
  
  public void setNodeId(long nodeId)
  {
    this.nodeId = nodeId;
  }
  
  public Properties getUpdateRequest()
  {
    return this.properties;
  }
  
  public void setUpdateRequest(Properties updateRequest)
  {
    this.properties = updateRequest;
  }
  
  public StandardNodePropertiesUpdateEvent(long standardId, long nodeId, Properties req)
  {
    this.standardId = standardId;
    this.nodeId = nodeId;
    this.properties = req;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardNodePropertiesUpdate(this);
  }
}
