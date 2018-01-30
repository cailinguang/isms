package com.vw.isms.standard.model.iso27001.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ApplicableProperty.Applicable;
import com.vw.isms.property.enums.SeverityLevelProperty;
import com.vw.isms.property.enums.SeverityLevelProperty.SeverityLevel;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.List;

public class ISO27001Question
  implements EvaluationNode
{
  private final StandardNode node;
  private final List<ISO27001Control> controls = new ArrayList();
  private final SeverityLevelProperty severity;
  private final ApplicableProperty applicable;
  
  public ISO27001Question(StandardNode node)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("iso27001_question")) {
      throw new ModelException("Unable to create ISO27001Question on " + node.getNodeType().getNodeType());
    }
    this.node = node;
    for (StandardNode child : node.getChildren()) {
      this.controls.add(new ISO27001Control(child));
    }
    this.severity = SeverityLevelProperty.getProperty(node, "severity");
    this.applicable = ApplicableProperty.getProperty(node, "applicable");
  }
  
  public float getScore()
  {
    if (this.applicable.getApplicable() != ApplicableProperty.Applicable.APPLICABLE) {
      return 0.0F;
    }
    float sum_control_score = 0.0F;
    for (ISO27001Control ctrl : this.controls) {
      sum_control_score += ctrl.getScore();
    }
    if (this.controls.size() > 0) {
      return sum_control_score / this.controls.size();
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
  
  public SeverityLevelProperty.SeverityLevel getSeverityLevel()
  {
    return this.severity.getSeverityLevel();
  }
  
  public ApplicableProperty.Applicable getApplicable()
  {
    return this.applicable.getApplicable();
  }
}
