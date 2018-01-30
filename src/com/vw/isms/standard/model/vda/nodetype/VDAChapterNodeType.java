package com.vw.isms.standard.model.vda.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.vda.evaluation.VDAChapter;
import com.vw.isms.util.IdUtil;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class VDAChapterNodeType
  extends StandardNodeType
{
  public static final String TYPE = "vda_chapter";
  private static final String DEFAULT_NAME_PROP = "vda_chapter_default_name";
  private static final String RENAME_LABEL_PROP = "vda_chapter_rename_label";
  private static final String DELETION_LABEL_PROP = "vda_chapter_delete_label";
  private static final String NEW_CHILD_LABEL_PROP = "vda_chapter_new_child_label";
  
  public VDAChapterNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "vda_chapter";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, IdUtil.next(), this);
    StringProperty.getProperty(node, "name").setValue(getDefaultName());
    return node;
  }
  
  public boolean allowMove()
  {
    return true;
  }
  
  public Set<? extends StandardNodeType> getAllowedParentNodeTypes()
  {
    return Collections.singleton(
      VDANodeTypeRegistry.getInstance().getRootNodeType());
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("vda_chapter_rename_label", "重命名");
  }
  
  public boolean allowDeletion()
  {
    return true;
  }
  
  public String getDeleteLabel()
  {
    return this.literals.getProperty("vda_chapter_delete_label", "删除");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return VDANodeTypeRegistry.getInstance().getQuestionNodeType();
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new VDAChapter(node);
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("vda_chapter_new_child_label", "新建问题");
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("vda_chapter_default_name", "Chapter");
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {}
}
