package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardNodeMoveEvent
  implements StandardEvent
{
  private final long standardId;
  private final long parentId;
  private final long nodeId;
  private final Map<Long, Integer> nodePositions = new HashMap();
  
  public StandardNodeMoveEvent(long standardId, long parentId, long nodeId, List<String> children)
  {
    this.standardId = standardId;
    this.parentId = parentId;
    this.nodeId = nodeId;
    for (int i = 0; i < children.size(); i++) {
      getNodePositions().put(Long.valueOf(Long.parseLong((String)children.get(i))), Integer.valueOf(i));
    }
  }
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public long getParentId()
  {
    return this.parentId;
  }
  
  public long getNodeId()
  {
    return this.nodeId;
  }
  
  public void process(StandardEventProcessor processor)
    throws EventProcessingException
  {
    processor.processStandardNodeMove(this);
  }
  
  public Map<Long, Integer> getNodePositions()
  {
    return this.nodePositions;
  }
}
