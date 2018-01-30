package com.vw.isms.standard.model;

import com.vw.isms.ModelException;
import com.vw.isms.standard.model.iso27001.ISO27001StandardType;
import com.vw.isms.standard.model.vda.VDAStandardType;
import java.util.Collection;

public abstract class StandardType
{
  public abstract String getType();
  
  public abstract Standard createTemplate()
    throws ModelException;
  
  public abstract StandardNodeType getRootNodeType();
  
  public abstract Collection<StandardNodeType> getNodeTypes();
  
  public abstract StandardNodeType getNodeType(String paramString)
    throws ModelException;
  
  public abstract Evaluation createEvaluation(Standard paramStandard)
    throws ModelException;
  
  public static StandardType getInstance(String type)
    throws ModelException
  {
    if (type.equals(VDAStandardType.getInstance().getType())) {
      return VDAStandardType.getInstance();
    }
    if (type.equals(ISO27001StandardType.getInstance().getType())) {
      return ISO27001StandardType.getInstance();
    }
    throw new ModelException("Unknown standard type: " + type);
  }
}
