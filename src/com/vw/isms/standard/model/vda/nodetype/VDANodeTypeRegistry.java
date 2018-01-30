package com.vw.isms.standard.model.vda.nodetype;

import com.vw.isms.ModelException;
import com.vw.isms.standard.model.StandardNodeType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class VDANodeTypeRegistry
{
  private final Properties literals;
  private final VDARootNodeType rootNodeType;
  private final VDAChapterNodeType chapterNodeType;
  private final VDAQuestionNodeType questionNodeType;
  private final VDALevelNodeType level1NodeType;
  private final VDALevelNodeType level2NodeType;
  private final VDALevelNodeType level3NodeType;
  private final VDALevelNodeType level4NodeType;
  private final VDALevelNodeType level5NodeType;
  private final VDAControlNodeType controlNodeType;
  private final Map<String, StandardNodeType> allNodeTypes = new HashMap();
  
  public VDANodeTypeRegistry(Properties literals)
  {
    this.literals = literals;
    this.rootNodeType = new VDARootNodeType(literals);
    this.chapterNodeType = new VDAChapterNodeType(literals);
    this.questionNodeType = new VDAQuestionNodeType(literals);
    this.level1NodeType = new VDALevelNodeType(literals, 1);
    this.level2NodeType = new VDALevelNodeType(literals, 2);
    this.level3NodeType = new VDALevelNodeType(literals, 3);
    this.level4NodeType = new VDALevelNodeType(literals, 4);
    this.level5NodeType = new VDALevelNodeType(literals, 5);
    this.controlNodeType = new VDAControlNodeType(literals);
    this.allNodeTypes.put(this.rootNodeType.getNodeType(), this.rootNodeType);
    this.allNodeTypes.put(this.chapterNodeType.getNodeType(), this.chapterNodeType);
    this.allNodeTypes.put(this.questionNodeType.getNodeType(), this.questionNodeType);
    this.allNodeTypes.put(this.level1NodeType.getNodeType(), this.level1NodeType);
    this.allNodeTypes.put(this.level2NodeType.getNodeType(), this.level2NodeType);
    this.allNodeTypes.put(this.level3NodeType.getNodeType(), this.level3NodeType);
    this.allNodeTypes.put(this.level4NodeType.getNodeType(), this.level4NodeType);
    this.allNodeTypes.put(this.level5NodeType.getNodeType(), this.level5NodeType);
    this.allNodeTypes.put(this.controlNodeType.getNodeType(), this.controlNodeType);
  }
  
  public Properties getLiterals()
  {
    return this.literals;
  }
  
  public VDARootNodeType getRootNodeType()
  {
    return this.rootNodeType;
  }
  
  public VDAChapterNodeType getChapterNodeType()
  {
    return this.chapterNodeType;
  }
  
  public VDAQuestionNodeType getQuestionNodeType()
  {
    return this.questionNodeType;
  }
  
  public VDALevelNodeType getLevel1NodeType()
  {
    return this.level1NodeType;
  }
  
  public VDALevelNodeType getLevel2NodeType()
  {
    return this.level2NodeType;
  }
  
  public VDALevelNodeType getLevel3NodeType()
  {
    return this.level3NodeType;
  }
  
  public VDALevelNodeType getLevel4NodeType()
  {
    return this.level4NodeType;
  }
  
  public VDALevelNodeType getLevel5NodeType()
  {
    return this.level5NodeType;
  }
  
  public VDAControlNodeType getControlNodeType()
  {
    return this.controlNodeType;
  }
  
  public Collection<StandardNodeType> getNodeTypes()
  {
    return this.allNodeTypes.values();
  }
  
  private static AtomicReference<VDANodeTypeRegistry> reference = new AtomicReference();
  private static final VDANodeTypeRegistry defaultInstance = new VDANodeTypeRegistry(new Properties());
  
  public static void initRegistry(Properties literals)
  {
    reference.set(new VDANodeTypeRegistry(literals));
  }
  
  public static VDANodeTypeRegistry getInstance()
  {
    VDANodeTypeRegistry instance = (VDANodeTypeRegistry)reference.get();
    if (instance != null) {
      return instance;
    }
    return defaultInstance;
  }
  
  public StandardNodeType getNodeType(String nodeType)
    throws ModelException
  {
    if (nodeType.startsWith("vda_level")) {
      nodeType = "vda_level";
    }
    if (this.allNodeTypes.containsKey(nodeType)) {
      return (StandardNodeType)this.allNodeTypes.get(nodeType);
    }
    throw new ModelException("Unknown vda node type: " + nodeType);
  }
}
