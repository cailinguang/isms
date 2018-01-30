package com.vw.isms.standard.model.iso27001.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class ISO27001NodeTypeRegistry
{
  private static AtomicReference<ISO27001NodeTypeRegistry> reference = new AtomicReference();
  private static final ISO27001NodeTypeRegistry defaultInstance = new ISO27001NodeTypeRegistry(new Properties());
  private final Properties literals;
  private final ISO27001RootNodeType rootNodeType;
  private final ISO27001ChapterNodeType chapterNodeType;
  private final ISO27001QuestionNodeType questionNodeType;
  private final ISO27001ControlNodeType controlNodeType;
  private final Map<String, StandardNodeType> allNodeTypes = new HashMap();
  
  public ISO27001NodeTypeRegistry(Properties literals)
  {
    this.literals = literals;
    this.rootNodeType = new ISO27001RootNodeType(literals);
    this.chapterNodeType = new ISO27001ChapterNodeType(literals);
    this.questionNodeType = new ISO27001QuestionNodeType(literals);
    this.controlNodeType = new ISO27001ControlNodeType(literals);
    this.allNodeTypes.put(this.rootNodeType.getNodeType(), this.rootNodeType);
    this.allNodeTypes.put(this.chapterNodeType.getNodeType(), this.chapterNodeType);
    this.allNodeTypes.put(this.questionNodeType.getNodeType(), this.questionNodeType);
    this.allNodeTypes.put(this.controlNodeType.getNodeType(), this.controlNodeType);
  }
  
  public Properties getLiterals()
  {
    return this.literals;
  }
  
  public ISO27001RootNodeType getRootNodeType()
  {
    return this.rootNodeType;
  }
  
  public ISO27001ChapterNodeType getChapterNodeType()
  {
    return this.chapterNodeType;
  }
  
  public ISO27001QuestionNodeType getQuestionNodeType()
  {
    return this.questionNodeType;
  }
  
  public ISO27001ControlNodeType getControlNodeType()
  {
    return this.controlNodeType;
  }
  
  public Collection<StandardNodeType> getNodeTypes()
  {
    return this.allNodeTypes.values();
  }
  
  public static void initRegistry(Properties literals)
  {
    reference.set(new ISO27001NodeTypeRegistry(literals));
  }
  
  public static ISO27001NodeTypeRegistry getInstance()
  {
    ISO27001NodeTypeRegistry instance = (ISO27001NodeTypeRegistry)reference.get();
    if (instance != null) {
      return instance;
    }
    return defaultInstance;
  }
  
  public StandardNodeType getNodeType(String nodeType)
    throws ModelException
  {
    if (this.allNodeTypes.containsKey(nodeType)) {
      return (StandardNodeType)this.allNodeTypes.get(nodeType);
    }
    throw new ModelException("Unknown vda node type: " + nodeType);
  }
}
