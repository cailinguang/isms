package com.vw.isms.standard.model.vda.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ApplicableProperty.Applicable;
import com.vw.isms.property.enums.LevelApprovedProperty;
import com.vw.isms.property.enums.LevelApprovedProperty.LevelApproved;
import com.vw.isms.property.enums.SeverityLevelProperty;
import com.vw.isms.property.enums.SeverityLevelProperty.SeverityLevel;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.List;

public class VDAQuestion
  implements EvaluationNode
{
  private final StandardNode node;
  private final List<VDALevel> levels = new ArrayList();
  private final SeverityLevelProperty severity;
  private final ApplicableProperty applicable;
  
  public VDAQuestion(StandardNode node)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("vda_question")) {
      throw new ModelException("Unable to create VDAQuestion on " + node.getNodeType().getNodeType());
    }
    this.node = node;
    if (node.getChildren().size() != 5) {
      throw new ModelException("VDAQuestion is expected to have 5 levels but actual is " + node.getChildren().size());
    }
    for (int i = 0; i < node.getChildren().size(); i++) {
      this.levels.add(new VDALevel((StandardNode)node.getChildren().get(i), i + 1));
    }
    this.severity = SeverityLevelProperty.getProperty(node, "severity");
    this.applicable = ApplicableProperty.getProperty(node, "applicable");
  }
  
  public float getScore()
  {
    if (this.applicable.getApplicable() != ApplicableProperty.Applicable.APPLICABLE) {
      return 0.0F;
    }
    float score = 0.0F;
    for (VDALevel level : this.levels)
    {
      score += level.getScore();
      if (level.getLevelApproved() == LevelApprovedProperty.LevelApproved.NO) {
        break;
      }
    }
    return score;
  }
  
  public StandardNode getStandardNode()
  {
    return this.node;
  }
  
  public List<? extends EvaluationNode> getChildren()
  {
    return this.levels;
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
