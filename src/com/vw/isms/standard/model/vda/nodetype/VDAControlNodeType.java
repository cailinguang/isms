package com.vw.isms.standard.model.vda.nodetype;

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
import com.vw.isms.standard.model.vda.evaluation.VDAControl;
import com.vw.isms.util.IdUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class VDAControlNodeType
  extends StandardNodeType
{
  public static final String TYPE = "vda_control";
  private static final String DEFAULT_NAME_PROP = "vda_control_default_name";
  private static final String DELETE_LABEL_PROP = "vda_control_delete_label";
  private static final String RENAME_LABEL_PROP = "vda_control_rename_label";
  private static final String DEFAULT_CREATE_PROP = "vda_control_default_create";
  public static final String PROP_COMPLIANCE = "compliance";
  public static final String PROP_COMPLIANCE_LEVEL = "complianceLevel";
  public static final String PROP_EVIDENCES = "evidences";
  
  public VDAControlNodeType(Properties literals)
  {
    super(literals);
  }
  
  public String getNodeType()
  {
    return "vda_control";
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
  
  public Set<StandardNodeType> getAllowedParentNodeTypes()
  {
    Set<StandardNodeType> sets = new HashSet();
    sets.add(VDANodeTypeRegistry.getInstance().getLevel1NodeType());
    sets.add(VDANodeTypeRegistry.getInstance().getLevel2NodeType());
    sets.add(VDANodeTypeRegistry.getInstance().getLevel3NodeType());
    sets.add(VDANodeTypeRegistry.getInstance().getControlNodeType());
    return sets;
  }
  
  public boolean allowRename()
  {
    return true;
  }
  
  public String getRenameLabel()
  {
    return this.literals.getProperty("vda_control_rename_label", "重命名");
  }
  
  public boolean allowDeletion()
  {
    return true;
  }
  
  public String getDeleteLabel()
  {
    return this.literals.getProperty("vda_control_delete_label", "删除");
  }
  
  public String getDefaultName()
  {
    return this.literals.getProperty("vda_control_default_name", "控制");
  }
  
  public boolean allowNewChild()
  {
    return true;
  }
  
  public String getNewChildLabel()
  {
    return this.literals.getProperty("vda_control_default_create", "新建子控制");
  }
  
  public StandardNodeType getNewChildNodeType()
  {
    return this;
  }
  
  public EvaluationNode createEvaluationNode(StandardNode node)
    throws ModelException
  {
    return new VDAControl(node);
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
