package com.vw.isms.standard.model;

public abstract interface Evaluation
{
  public abstract float getScore();
  
  public abstract Standard getStandard();
  
  public abstract float getMaxScore();
}
