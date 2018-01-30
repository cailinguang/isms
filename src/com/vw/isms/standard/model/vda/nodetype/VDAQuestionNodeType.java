package com.vw.isms.standard.model.vda.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.SeverityLevelProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.vda.evaluation.VDAQuestion;
import com.vw.isms.util.IdUtil;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class VDAQuestionNodeType
  extends StandardNodeType
{
  public static final String TYPE = "vda_question";
  private static final String DEFAULT_NAME_PROP = "vda_question_default_name";
  private static final String RENAME_LABEL_PROP = "vda_question_rename_label";
  private static final String DELETE_LABLE_PROP = "vda_question_delete_label";
  public static final String PROP_SEVERITY = "severity";
  public static final String PROP_APPLICABLE = "applicable";
  
  public VDAQuestionNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "vda_question";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode question = new StandardNode(standard, IdUtil.next(), this);
    StringProperty.getProperty(question, "name").setValue(getDefaultName());
    question.newChild(VDANodeTypeRegistry.getInstance().getLevel1NodeType(), 0);
    question.newChild(VDANodeTypeRegistry.getInstance().getLevel2NodeType(), 1);
    question.newChild(VDANodeTypeRegistry.getInstance().getLevel3NodeType(), 2);
    question.newChild(VDANodeTypeRegistry.getInstance().getLevel4NodeType(), 3);
    question.newChild(VDANodeTypeRegistry.getInstance().getLevel5NodeType(), 4);
    return question;
  }
  
  public boolean allowMove()
  {
    return true;
  }
  
  public Set<? extends StandardNodeType> getAllowedParentNodeTypes()
  {
    return Collections.singleton(
      VDANodeTypeRegistry.getInstance().getChapterNodeType());
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("vda_question_rename_label", "重命名");
  }
  
  public boolean allowDeletion()
  {
    return true;
  }
  
  public String getDeleteLabel()
  {
    return this.literals.getProperty("vda_question_delete_label", "删除");
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new VDAQuestion(node);
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("vda_question_default_name", "Question");
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
