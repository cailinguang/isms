package com.vw.isms.standard.model.iso27001.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.enums.ComplianceLevelProperty;
import com.vw.isms.property.enums.ComplianceLevelProperty.ComplianceLevel;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.List;

public class ISO27001Control
  implements EvaluationNode
{
  private final StandardNode node;
  private final List<ISO27001Control> controls = new ArrayList();
  private FloatProperty compliance;
  private ComplianceLevelProperty complianceLevel;
  
  public ISO27001Control(StandardNode node)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("iso27001_control")) {
      throw new ModelException("Unable to create ISO27001Control on " + node.getNodeType().getNodeType());
    }
    this.node = node;
    for (StandardNode child : node.getChildren()) {
      this.controls.add(new ISO27001Control(child));
    }
    if (this.controls.isEmpty())
    {
      this.compliance = FloatProperty.getProperty(node, "compliance");
      this.complianceLevel = ComplianceLevelProperty.getProperty(node, "complianceLevel");
    }
  }
  
  public float getScore()
  {
    if (this.controls.isEmpty()) {
      return getComplianceScore();
    }
    float sum_control_score = 0.0F;
    for (ISO27001Control ctrl : this.controls) {
      sum_control_score += ctrl.getScore();
    }
    return sum_control_score / this.controls.size();
  }
  
  private float getComplianceScore()
  {
    if (this.complianceLevel.getComplianceLevel() == ComplianceLevelProperty.ComplianceLevel.COMPLIANT) {
      return 1.0F;
    }
    if ((this.complianceLevel.getComplianceLevel() == ComplianceLevelProperty.ComplianceLevel.OTHER) && 
      (this.compliance.getValue() >= 100.0D)) {
      return 1.0F;
    }
    return 0.0F;
  }
  
  public StandardNode getStandardNode()
  {
    return this.node;
  }
  
  public List<? extends EvaluationNode> getChildren()
  {
    return this.controls;
  }
}
