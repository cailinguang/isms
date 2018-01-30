package com.vw.isms.standard.model.iso27001.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.iso27001.evaluation.ISO27001Evaluation;
import java.util.Properties;

public class ISO27001RootNodeType
  extends StandardNodeType
{
  private static final String TYPE = "iso27001_root";
  private static final String DEFAULT_NAME_PROP = "iso27001_root_default_name";
  private static final String RENAME_LABEL_PROP = "iso27001_root_rename_label";
  private static final String NEW_CHILD_LABEL_PROP = "iso27001_root_new_child_label";
  
  public ISO27001RootNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "iso27001_root";
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("iso27001_root_rename_label", "重命名");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("iso27001_root_new_child_label", "新建章节");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return ISO27001NodeTypeRegistry.getInstance().getChapterNodeType();
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new ISO27001Evaluation(node.getStandard());
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, 0L, this);
    StringProperty.getProperty(node, "name").setValue(getDefaultName());
    return node;
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("iso27001_root_default_name", "ISO27001 Standard");
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {}
}
