package com.vw.isms.standard.model;

public class JSTreeNode
{
  private final String id;
  private final String type;
  private final String text;
  private final String parent;
  
  public class Data
  {
    public Data() {}
    
    public String getType()
    {
      return JSTreeNode.this.type;
    }
  }
  
  public JSTreeNode(String id, String parent, String text, String type)
  {
    this.id = id;
    this.parent = parent;
    this.text = text;
    this.type = type;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public Data getData()
  {
    return new Data();
  }
  
  public String getText()
  {
    return this.text;
  }
  
  public String getParent()
  {
    return this.parent;
  }
}
