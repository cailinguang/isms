package com.vw.isms.standard.model.vda;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.Evaluation;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.StandardType;
import com.vw.isms.standard.model.vda.evaluation.VDAEvaluation;
import com.vw.isms.standard.model.vda.nodetype.VDANodeTypeRegistry;
import com.vw.isms.util.IdUtil;
import java.util.Collection;

public class VDAStandardType
  extends StandardType
{
  private static final String TYPE = "vda";
  private static final VDAStandardType INSTANCE = new VDAStandardType();
  
  public static VDAStandardType getInstance()
  {
    return INSTANCE;
  }
  
  public String getType()
  {
    return "vda";
  }
  
  public Standard createTemplate()
    throws ModelException
  {
    Standard standard = new Standard(IdUtil.next(), this, false);
    StringProperty.getProperty(standard, "name").setValue(getRootNodeType().getDefaultName());
    return standard;
  }
  
  public StandardNodeType getRootNodeType()
  {
    return VDANodeTypeRegistry.getInstance().getRootNodeType();
  }
  
  public Collection<StandardNodeType> getNodeTypes()
  {
    return VDANodeTypeRegistry.getInstance().getNodeTypes();
  }
  
  public Evaluation createEvaluation(Standard standard)
    throws ModelException
  {
    if (standard.getStandardType().getType() != "vda") {
      throw new ModelException("Unable to create vda evaluation from template of " + standard.getStandardType().getType());
    }
    return new VDAEvaluation(standard);
  }
  
  public StandardNodeType getNodeType(String nodeType)
    throws ModelException
  {
    return VDANodeTypeRegistry.getInstance().getNodeType(nodeType);
  }
}
