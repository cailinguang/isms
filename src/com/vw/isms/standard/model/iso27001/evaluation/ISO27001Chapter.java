package com.vw.isms.standard.model.iso27001.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ApplicableProperty.Applicable;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ISO27001Chapter
  implements EvaluationNode
{
  private final StandardNode node;
  private final List<ISO27001Question> questions = new ArrayList();
  
  public ISO27001Chapter(StandardNode node)
    throws ModelException
  {
    if (!node.getNodeType().getNodeType().equals("iso27001_chapter")) {
      throw new ModelException("Unable to create ISO27001Chapter on " + node.getNodeType().getNodeType());
    }
    this.node = node;
    for (StandardNode child : node.getChildren()) {
      this.questions.add(new ISO27001Question(child));
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
    for (ISO27001Question question : this.questions) {
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
  
  public List<ISO27001Question> getQuestions()
  {
    return Collections.unmodifiableList(this.questions);
  }
}
