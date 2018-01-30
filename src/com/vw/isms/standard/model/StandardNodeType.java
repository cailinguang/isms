package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public abstract class StandardNodeType
{
  protected final Properties literals;
  
  public StandardNodeType(Properties literals)
  {
    this.literals = literals;
  }
  
  public abstract String getNodeType();
  
  public abstract StandardNode createTemplateNode(Standard paramStandard)
    throws ModelException;
  
  public abstract String getDefaultName();
  
  public abstract void addEvaluationProperties(StandardNode paramStandardNode)
    throws ModelException;
  
  public boolean allowMove()
  {
    return false;
  }
  
  public Set<? extends StandardNodeType> getAllowedParentNodeTypes()
  {
    return Collections.emptySet();
  }
  
  public boolean allowRename()
  {
    return false;
  }
  
  public String getRenameLabel()
  {
    return "";
  }
  
  public boolean allowDeletion()
  {
    return false;
  }
  
  public String getDeleteLabel()
  {
    return "";
  }
  
  public boolean allowNewChild()
  {
    return false;
  }
  
  public String getNewChildLabel()
  {
    return "";
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return null;
  }
  
  public abstract EvaluationNode createEvaluationNode(StandardNode paramStandardNode)
    throws ModelException;
}
