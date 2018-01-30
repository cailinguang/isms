package com.vw.isms.standard.model;

import com.vw.isms.ModelException;

public abstract interface StandardNodeCustomizer
{
  public abstract void customize(StandardNode paramStandardNode)
    throws ModelException;
}
