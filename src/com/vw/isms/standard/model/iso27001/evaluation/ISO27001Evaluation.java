package com.vw.isms.standard.model.iso27001.evaluation;

import com.vw.isms.ModelException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ApplicableProperty.Applicable;
import com.vw.isms.standard.model.Evaluation;
import com.vw.isms.standard.model.EvaluationNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ISO27001Evaluation
  implements Evaluation, EvaluationNode
{
  private final Standard standard;
  private final List<ISO27001Chapter> chapters = new ArrayList();
  
  public ISO27001Evaluation(Standard standard)
    throws ModelException
  {
    this.standard = standard;
    for (StandardNode node : standard.getChildren()) {
      this.chapters.add(new ISO27001Chapter(node));
    }
  }
  
  public float getScore()
  {
    float score = 0.0F;
    int count = 0;
    for (ISO27001Chapter chapter : getChapters()) {
      for (ISO27001Question question : chapter.getQuestions()) {
        if (question.getApplicable() != ApplicableProperty.Applicable.NOT_APPLICABLE)
        {
          score += question.getScore();
          count++;
        }
      }
    }
    if (count > 0) {
      return score / count;
    }
    return 0.0F;
  }
  
  public StandardNode getStandardNode()
    throws ModelException
  {
    return this.standard.getRootNode();
  }
  
  public List<? extends EvaluationNode> getChildren()
  {
    return this.chapters;
  }
  
  public Standard getStandard()
  {
    return this.standard;
  }
  
  public float getMaxScore()
  {
    return 1.0F;
  }
  
  public List<ISO27001Chapter> getChapters()
  {
    return Collections.unmodifiableList(this.chapters);
  }
  
  public int getTotalQuestions()
  {
    int count = 0;
    for (ISO27001Chapter chapter : getChapters()) {
      count += chapter.getQuestions().size();
    }
    return count;
  }
  
  public int getEvaluatedQuestions()
  {
    int count = 0;
    for (ISO27001Chapter chapter : getChapters()) {
      for (ISO27001Question question : chapter.getQuestions()) {
        if (question.getApplicable() != ApplicableProperty.Applicable.NOT_EVALUATED) {
          count++;
        }
      }
    }
    return count;
  }
}
