package com.vw.isms.standard.model.vda.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.vda.evaluation.VDAEvaluation;
import java.util.Properties;

public class VDARootNodeType
  extends StandardNodeType
{
  private static final String TYPE = "vda_root";
  private static final String DEFAULT_NAME_PROP = "vda_root_default_name";
  private static final String RENAME_LABEL_PROP = "vda_root_rename_label";
  private static final String NEW_CHILD_LABEL_PROP = "vda_root_new_child_label";
  
  public VDARootNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "vda_root";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, 0L, this);
    StringProperty.getProperty(node, "name").setValue(getDefaultName());
    return node;
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("vda_root_rename_label", "重命名");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("vda_root_new_child_label", "章节");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return VDANodeTypeRegistry.getInstance().getChapterNodeType();
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new VDAEvaluation(node.getStandard());
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("vda_root_default_name", "VDA Standard");
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {}
}
