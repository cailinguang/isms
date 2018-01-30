package com.vw.isms.standard.model.iso27001.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.SeverityLevelProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.iso27001.evaluation.ISO27001Question;
import com.vw.isms.util.IdUtil;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class ISO27001QuestionNodeType
  extends StandardNodeType
{
  public static final String TYPE = "iso27001_question";
  private static final String DEFAULT_NAME_PROP = "iso27001_question_default_name";
  private static final String RENAME_LABEL_PROP = "iso27001_question_rename_label";
  private static final String DELETE_LABLE_PROP = "iso27001_question_delete_label";
  private static final String NEW_CHILD_LABEL_PROP = "iso27001_question_child_label";
  public static final String PROP_SEVERITY = "severity";
  public static final String PROP_APPLICABLE = "applicable";
  
  public ISO27001QuestionNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "iso27001_question";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode question = new StandardNode(standard, IdUtil.next(), this);
    StringProperty.getProperty(question, "name").setValue(getDefaultName());
    return question;
  }
  
  public boolean allowMove()
  {
    return true;
  }
  
  public Set<? extends StandardNodeType> getAllowedParentNodeTypes()
  {
    return Collections.singleton(
      ISO27001NodeTypeRegistry.getInstance().getChapterNodeType());
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("iso27001_question_rename_label", "重命名");
  }
  
  public boolean allowDeletion()
  {
    return true;
  }
  
  public String getDeleteLabel()
  {
    return this.literals.getProperty("iso27001_question_delete_label", "删除");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("iso27001_question_child_label", "新建控制");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return ISO27001NodeTypeRegistry.getInstance().getControlNodeType();
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new ISO27001Question(node);
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("iso27001_question_default_name", "Question");
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {
    SeverityLevelProperty severity = new SeverityLevelProperty(IdUtil.next(), "severity", false);
    node.addProperty(severity);
    ApplicableProperty isApplicable = new ApplicableProperty(IdUtil.next(), "applicable", false);
    node.addProperty(isApplicable);
  }
}
