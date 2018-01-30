package com.vw.isms.standard.model.iso27001.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.property.EvidenceSetProperty;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.StringProperty;
import com.vw.isms.property.enums.ComplianceLevelProperty;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Evidence;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.model.iso27001.evaluation.ISO27001Control;
import com.vw.isms.util.IdUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ISO27001ControlNodeType
  extends StandardNodeType
{
  public static final String TYPE = "iso27001_control";
  private static final String DEFAULT_NAME_PROP = "iso27001_control_default_name";
  private static final String DELETE_LABEL_PROP = "iso27001_control_delete_label";
  private static final String RENAME_LABEL_PROP = "iso27001_control_rename_label";
  private static final String DEFAULT_CREATE_PROP = "iso27001_control_default_create";
  public static final String PROP_COMPLIANCE = "compliance";
  public static final String PROP_COMPLIANCE_LEVEL = "complianceLevel";
  public static final String PROP_EVIDENCES = "evidences";
  
  public ISO27001ControlNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "iso27001_control";
  }
  
  public StandardNode createTemplateNode(Standard standard)
    throws ModelException
  {
    StandardNode node = new StandardNode(standard, IdUtil.next(), this);
    StringProperty.getProperty(node, "name").setValue(getDefaultName());
    return node;
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("iso27001_control_rename_label", "重命名");
  }
  
  public boolean allowDeletion()
  {
    return true;
  }
  
  public String getDeleteLabel()
  {
    return this.literals.getProperty("iso27001_control_delete_label", "删除");
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("iso27001_control_default_name", "Control");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("iso27001_control_default_create", "新建子控制");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return this;
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new ISO27001Control(node);
  }
  
  public void addEvaluationProperties(StandardNode node)
    throws ModelException
  {
    if (node.getChildren().isEmpty())
    {
      FloatProperty compliance = new FloatProperty(IdUtil.next(), "compliance", false);
      node.addProperty(compliance);
      ComplianceLevelProperty complianceLevel = new ComplianceLevelProperty(IdUtil.next(), "complianceLevel", false);
      node.addProperty(complianceLevel);
      Collection<Evidence> evidences = Collections.emptyList();
      node.addProperty(new EvidenceSetProperty(IdUtil.next(), "evidences", false, evidences));
    }
  }
}
