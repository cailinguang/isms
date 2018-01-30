package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import com.vw.isms.property.Properties;
import com.vw.isms.property.StringProperty;
import com.vw.isms.util.IdUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Standard
  extends Properties
{
  private final long standardId;
  private final StandardType standardType;
  private final boolean isEvaluation;
  private boolean archived;
  private final List<StandardNode> children = new ArrayList();
  
  public Standard(long standardId, StandardType standardType, boolean isEvaluation)
  {
    this.standardId = standardId;
    this.standardType = standardType;
    this.isEvaluation = isEvaluation;
    addProperty(new StringProperty(0L, "name", false));
    addProperty(new StringProperty(0L, "description", false));
  }
  
  public long getStandardId()
  {
    return this.standardId;
  }
  
  public StandardType getStandardType()
  {
    return this.standardType;
  }
  
  public boolean isEvaluation()
  {
    return this.isEvaluation;
  }
  
  public void addChild(StandardNode child)
  {
    this.children.add(child);
  }
  
  public List<StandardNode> getChildren()
  {
    return Collections.unmodifiableList(this.children);
  }
  
  public Map<String, JSTreeMetadata> getJSTreeMetadata()
  {
    Map<String, JSTreeMetadata> metadata = new HashMap();
    for (StandardNodeType nodeType : this.standardType.getNodeTypes()) {
      metadata.put(nodeType.getNodeType(), new JSTreeMetadata(this, nodeType));
    }
    return metadata;
  }
  
  public void collectJSTreeNodes(List<JSTreeNode> jstreeNodes)
    throws ModelException
  {
    jstreeNodes.add(new JSTreeNode("0", "#", StringProperty.getProperty(this, "name").getValue(), this.standardType
      .getRootNodeType().getNodeType()));
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
  
  public Standard forkEvaluation()
    throws ModelException
  {
    StandardNodeCustomizer customizer = new StandardNodeCustomizer()
    {
      public void customize(StandardNode node)
        throws ModelException
      {
        node.getNodeType().addEvaluationProperties(node);
      }
    };
    Standard standard = new Standard(IdUtil.next(), this.standardType, true);
    standard.copyPropertiesFrom(this);
    for (StandardNode child : this.children) {
      standard.addChild(child.deepCopy(standard, customizer));
    }
    return standard;
  }
  
  public StandardNode getRootNode()
    throws ModelException
  {
    StandardNode root = this.standardType.getRootNodeType().createTemplateNode(this);
    root.copyPropertiesFrom(this);
    return root;
  }
  
  public String getName()
    throws ModelException
  {
    return StringProperty.getProperty(this, "name").getValue();
  }
  
  public boolean isArchived()
  {
    return this.archived;
  }
  
  public void setArchived(boolean archived)
  {
    this.archived = archived;
  }
}
