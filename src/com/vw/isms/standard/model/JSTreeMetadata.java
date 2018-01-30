package com.vw.isms.standard.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSTreeMetadata
{
  private final Standard standard;
  private final StandardNodeType nodeType;
  
  private class Move
  {
    private Move() {}
    
    public String getUri()
    {
      return "/api/standards/" + JSTreeMetadata.this.standard.getStandardId() + "/nodes/{{id}}";
    }
    
    public String getAction()
    {
      return "PATCH";
    }
    
    @JsonGetter("allowed_parent_types")
    public List<String> getAllowedParentTypes()
    {
      List<String> types = new ArrayList();
      for (StandardNodeType parentType : JSTreeMetadata.this.nodeType.getAllowedParentNodeTypes()) {
        types.add(parentType.getNodeType());
      }
      return types;
    }
  }
  
  private class Create
  {
    private Create() {}
    
    public String getUri()
    {
      return "/api/standards/" + JSTreeMetadata.this.standard.getStandardId() + "/nodes";
    }
    
    public String getAction()
    {
      return "POST";
    }
    
    public String getLabel()
    {
      return JSTreeMetadata.this.nodeType.getNewChildLabel();
    }
  }
  
  private class Remove
  {
    private Remove() {}
    
    public String getUri()
    {
      return "/api/standards/" + JSTreeMetadata.this.standard.getStandardId() + "/nodes/{{id}}";
    }
    
    public String getAction()
    {
      return "DELETE";
    }
    
    public String getLabel()
    {
      return JSTreeMetadata.this.nodeType.getDeleteLabel();
    }
  }
  
  private class Rename
  {
    private Rename() {}
    
    public String getUri()
    {
      return "/api/standards/" + JSTreeMetadata.this.standard.getStandardId() + "/nodes/{{id}}";
    }
    
    public String getAction()
    {
      return "PATCH";
    }
    
    public String getLabel()
    {
      return JSTreeMetadata.this.nodeType.getRenameLabel();
    }
  }
  
  public JSTreeMetadata(Standard standard, StandardNodeType nodeType)
  {
    this.standard = standard;
    this.nodeType = nodeType;
  }
  
  public Move getMove()
  {
    if ((this.nodeType.allowMove()) && (!this.nodeType.getAllowedParentNodeTypes().isEmpty())) {
      return new Move();
    }
    return null;
  }
  
  public Create getCreate()
  {
    if (this.nodeType.allowNewChild()) {
      return new Create();
    }
    return null;
  }
  
  public Remove getRemove()
  {
    if (this.nodeType.allowDeletion()) {
      return new Remove();
    }
    return null;
  }
  
  public Rename getRename()
  {
    if (this.nodeType.allowRename()) {
      return new Rename();
    }
    return null;
  }
}
