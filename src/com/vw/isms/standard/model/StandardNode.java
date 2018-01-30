package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import com.vw.isms.property.Properties;
import com.vw.isms.property.StringProperty;
import com.vw.isms.util.IdUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StandardNode
  extends Properties
{
  private final long nodeId;
  private final Standard standard;
  private final StandardNodeType nodeType;
  private int nodePosition;
  private StandardNode parent;
  private long parentId;
  private final List<StandardNode> children = new ArrayList();
  
  public StandardNode(Standard standard, long nodeId, StandardNodeType nodeType)
  {
    this.standard = standard;
    this.nodeId = nodeId;
    this.nodeType = nodeType;
    addProperty(new StringProperty(IdUtil.next(), "name", false));
    addProperty(new StringProperty(IdUtil.next(), "description", false));
    if (standard.isEvaluation()) {
      addProperty(new StringProperty(IdUtil.next(), "comment", false));
    }
  }
  
  public StandardNodeType getNodeType()
  {
    return this.nodeType;
  }
  
  public StandardNode getParent()
  {
    return this.parent;
  }
  
  public long getNodeId()
  {
    return this.nodeId;
  }
  
  public void setNodePosition(int nodePosition)
  {
    this.nodePosition = nodePosition;
  }
  
  public int getNodePosition()
  {
    return this.nodePosition;
  }
  
  public void setParentId(long parentId)
  {
    this.parentId = parentId;
  }
  
  public long getParentId()
  {
    if (this.parent != null) {
      return this.parent.getNodeId();
    }
    return this.parentId;
  }
  
  public void addChild(StandardNode node)
    throws ModelException
  {
    if (node.getStandard().getStandardId() != getStandard().getStandardId()) {
      throw new ModelException("Cannot move node across standard");
    }
    if (node.parent != null) {
      node.parent.removeChild(node);
    }
    node.parent = this;
    this.children.add(node);
  }
  
  public List<StandardNode> getChildren()
  {
    return Collections.unmodifiableList(this.children);
  }
  
  public void removeChild(StandardNode node)
  {
    this.children.remove(node);
    node.parent = null;
  }
  
  public Standard getStandard()
  {
    return this.standard;
  }
  
  public StandardNode newChild(StandardNodeType childNodeType, int childPos)
    throws ModelException
  {
    StandardNode child = childNodeType.createTemplateNode(this.standard);
    child.setNodePosition(childPos);
    this.children.add(child);
    child.parent = this;
    return child;
  }
  
  public void collectJSTreeNodes(List<JSTreeNode> jstreeNodes)
    throws ModelException
  {
    jstreeNodes.add(new JSTreeNode(Long.toString(getNodeId()), 
      Long.toString(getParentId()), StringProperty.getProperty(this, "name").getValue(), 
      getNodeType().getNodeType()));
    sortChildren();
    for (StandardNode child : this.children) {
      child.collectJSTreeNodes(jstreeNodes);
    }
  }
  
  public void sortChildren()
  {
    Collections.sort(this.children, new Comparator<StandardNode>()
    {
      public int compare(StandardNode n1, StandardNode n2)
      {
        if (n1.getNodePosition() != n2.getNodePosition()) {
          return n1.getNodePosition() - n2.getNodePosition();
        }
        return (int)(n1.getNodeId() - n2.getNodeId());
      }
    });
  }
  
  public StandardNode deepCopy(Standard standard, StandardNodeCustomizer customizer)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, IdUtil.next(), this.nodeType);
    node.nodePosition = this.nodePosition;
    node.copyPropertiesFrom(this);
    for (StandardNode child : this.children) {
      node.addChild(child.deepCopy(standard, customizer));
    }
    customizer.customize(node);
    return node;
  }
  
  public String getName()
    throws ModelException
  {
    return StringProperty.getProperty(this, "name").getValue();
  }
}
