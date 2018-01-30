package com.vw.isms.standard.model.iso27001;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.Evaluation;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.StandardType;
import com.vw.isms.standard.model.iso27001.evaluation.ISO27001Evaluation;
import com.vw.isms.standard.model.iso27001.nodetype.ISO27001NodeTypeRegistry;
import com.vw.isms.util.IdUtil;
import java.util.Collection;

public class ISO27001StandardType
  extends StandardType
{
  private static final String TYPE = "iso27001";
  private static final ISO27001StandardType INSTANCE = new ISO27001StandardType();
  
  public static ISO27001StandardType getInstance()
  {
    return INSTANCE;
  }
  
  public String getType()
  {
    return "iso27001";
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
    return ISO27001NodeTypeRegistry.getInstance().getRootNodeType();
  }
  
  public Collection<StandardNodeType> getNodeTypes()
  {
    return ISO27001NodeTypeRegistry.getInstance().getNodeTypes();
  }
  
  public StandardNodeType getNodeType(String nodeType)
    throws ModelException
  {
    return ISO27001NodeTypeRegistry.getInstance().getNodeType(nodeType);
  }
  
  public Evaluation createEvaluation(Standard standard)
    throws ModelException
  {
    if (standard.getStandardType().getType() != "iso27001") {
      throw new ModelException("Unable to create iso27001 evaluation from template of " + standard.getStandardType().getType());
    }
    return new ISO27001Evaluation(standard);
  }
}
