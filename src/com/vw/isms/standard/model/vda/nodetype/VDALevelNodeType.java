package com.vw.isms.standard.model.vda.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.property.enums.LevelApprovedProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.vda.evaluation.VDALevel;
import com.vw.isms.util.IdUtil;
import java.util.Properties;

public class VDALevelNodeType
  extends StandardNodeType
{
  public static final String TYPE = "vda_level";
  private static final String DEFAULT_NAME_PROP_PREFIX = "vda_level_default_name";
  private static final String NEW_CHILD_LABEL_PROP = "vda_level_new_child_label";
  public static final String PROP_LEVEL_APPROVED = "levelApproved";
  private final int index;
  
  public VDALevelNodeType(Properties literals, int index)
  {
    super(literals);
    this.index = index;
  }
  
  public String getNodeType()
  {
    return "vda_level";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, IdUtil.next(), this);
    StringProperty.getProperty(node, "name").setValue(getDefaultName());
    return node;
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("vda_level_new_child_label", "新建控制");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return VDANodeTypeRegistry.getInstance().getControlNodeType();
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new VDALevel(node, this.index);
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("vda_level_default_name" + this.index, "Level " + this.index);
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {
    LevelApprovedProperty levelApproved = new LevelApprovedProperty(IdUtil.next(), "levelApproved", false);
    node.addProperty(levelApproved);
  }
}
