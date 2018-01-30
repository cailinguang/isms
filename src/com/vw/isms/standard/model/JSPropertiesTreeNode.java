package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import com.vw.isms.property.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSPropertiesTreeNode
{
  private final String id;
  private final String uri;
  private final String type;
  private final EvaluationNode node;
  private final List<JSPropertiesTreeNode> children = new ArrayList();
  
  public JSPropertiesTreeNode(EvaluationNode node)
    throws ModelException
  {
    this.node = node;
    this.id = Long.toString(node.getStandardNode().getNodeId());
    
    this.uri = ("/api/standards/" + node.getStandardNode().getStandard().getStandardId() + "/nodes/" + node.getStandardNode().getNodeId() + "/properties");
    this.type = node.getStandardNode().getNodeType().getNodeType();
    for (EvaluationNode child : node.getChildren()) {
      this.children.add(new JSPropertiesTreeNode(child));
    }
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getUri()
  {
    return this.uri;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public float getScore()
  {
    return this.node.getScore();
  }
  
  public Map<String, Property> getProperties()
    throws ModelException
  {
    return this.node.getStandardNode().getProperties();
  }
  
  public List<JSPropertiesTreeNode> getChildren()
  {
    return this.children;
  }
}
