package com.vw.isms.standard.model.vda.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.LevelApprovedProperty;
import com.vw.isms.property.enums.LevelApprovedProperty.LevelApproved;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.List;

public class VDALevel
  implements EvaluationNode
{
  private final StandardNode node;
  private final int index;
  private final List<VDAControl> controls = new ArrayList();
  private final LevelApprovedProperty levelApproved;
  
  public VDALevel(StandardNode node, int index)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("vda_level")) {
      throw new ModelException("Unable to create VDALevel on " + node.getNodeType().getNodeType() + " " + "vda_level");
    }
    this.node = node;
    this.index = index;
    for (StandardNode child : node.getChildren()) {
      this.controls.add(new VDAControl(child));
    }
    this.levelApproved = LevelApprovedProperty.getProperty(node, "levelApproved");
  }
  
  public float getScore()
  {
    if (this.levelApproved.getLevelApproved() == LevelApprovedProperty.LevelApproved.YES) {
      return 1.0F;
    }
    float sum_control_score = 0.0F;
    for (VDAControl ctrl : this.controls) {
      sum_control_score += ctrl.getScore();
    }
    if (this.controls.size() > 0) {
      return sum_control_score / this.controls.size() * 0.9F;
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
  
  public int getIndex()
  {
    return this.index;
  }
  
  public LevelApprovedProperty.LevelApproved getLevelApproved()
  {
    return this.levelApproved.getLevelApproved();
  }
}
