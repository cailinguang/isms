package com.vw.isms.standard.model.vda.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ApplicableProperty.Applicable;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VDAChapter
  implements EvaluationNode
{
  private final StandardNode node;
  private final List<VDAQuestion> questions = new ArrayList();
  
  public VDAChapter(StandardNode node)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("vda_chapter")) {
      throw new ModelException("Unable to create VDAChapter on " + node.getNodeType().getNodeType());
    }
    this.node = node;
    for (StandardNode child : node.getChildren()) {
      this.questions.add(new VDAQuestion(child));
    }
  }
  
  public StandardNode getStandardNode()
  {
    return this.node;
  }
  
  public List<? extends EvaluationNode> getChildren()
  {
    return this.questions;
  }
  
  public float getScore()
  {
    float score = 0.0F;
    int count = 0;
    for (VDAQuestion question : this.questions) {
      if (question.getApplicable() != ApplicableProperty.Applicable.NOT_APPLICABLE)
      {
        score += question.getScore();
        count++;
      }
    }
    if (count > 0) {
      return score / count;
    }
    return 0.0F;
  }
  
  public List<VDAQuestion> getQuestions()
  {
    return Collections.unmodifiableList(this.questions);
  }
}
