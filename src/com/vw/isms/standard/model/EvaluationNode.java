package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import java.util.List;

public abstract interface EvaluationNode
{
  public abstract float getScore();
  
  public abstract StandardNode getStandardNode()
    throws ModelException;
  
  public abstract List<? extends EvaluationNode> getChildren();
}
