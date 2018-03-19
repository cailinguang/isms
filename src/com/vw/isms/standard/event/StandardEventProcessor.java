package com.vw.isms.standard.event;

import com.vw.isms.EventProcessingException;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.Property;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeCustomizer;
import com.vw.isms.standard.model.StandardNodeType;
import com.vw.isms.standard.repository.StandardRepository;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class StandardEventProcessor
{
  private final StandardRepository repository;
  
  public StandardEventProcessor(StandardRepository repository)
  {
    this.repository = repository;
  }
  
  public void processStandardCreation(StandardCreationEvent event)
    throws EventProcessingException
  {
    try
    {
      Standard standard = event.getStandardType().createTemplate();
      this.repository.createStandard(standard);
    }
    catch (RepositoryException e)
    {
      throw new EventProcessingException(e);
    }
    catch (ModelException e)
    {
      throw new EventProcessingException(e);
    }
  }
  
  public void processStandardNodeRename(StandardNodeRenameEvent event)
    throws EventProcessingException
  {
    try
    {
      if (event.getNodeId() == 0L)
      {
        Standard standard = this.repository.getStandardWithoutNodes(event.getStandardId());
        StringProperty.getProperty(standard, "name").setValue(event.getNewName());
        this.repository.updateStandardWithoutChildren(standard);
      }
      else
      {
        StandardNode node = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), event.getNodeId());
        if (!node.getNodeType().allowRename()) {
          throw new EventProcessingException("Disallow manual rename of node type: " + node.getNodeType().getNodeType());
        }
        StringProperty name = StringProperty.getProperty(node, "name");
        name.setValue(event.getNewName());
        name.update(node, this.repository);
      }
    }
    catch (RepositoryException e)
    {
      throw new EventProcessingException(e);
    }
    catch (ModelException e)
    {
      throw new EventProcessingException(e);
    }
  }
  
  public void processStandardNodeCreation(StandardNodeCreationEvent event)
    throws EventProcessingException
  {
    try
    {
      StandardNode parent = null;
      long parentId = Long.parseLong(event.getRequest().getParentId());
      if (parentId != 0L)
      {
        parent = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), parentId);
      }
      else
      {
        Standard standard = this.repository.getStandardWithoutNodes(event.getStandardId());
        parent = standard.getRootNode();
      }
      if (!parent.getNodeType().allowNewChild()) {
        throw new EventProcessingException("Disallow manual creation of child node under node type: " + parent.getNodeType().getNodeType());
      }
      if (event.getRequest().isCreateNew()) {
        createDefaultNewNode(parent, event);
      } else if (event.getRequest().isAppendChildren()) {
        duplicateChildren(parent, event);
      } else if (event.getRequest().isDuplicate()) {
        duplicateNode(parent, event);
      }
    }
    catch (RepositoryException e)
    {
      throw new EventProcessingException(e);
    }
    catch (ModelException e)
    {
      throw new EventProcessingException(e);
    }
  }
  
  private void createDefaultNewNode(StandardNode parent, StandardNodeCreationEvent event)
    throws RepositoryException, EventProcessingException, ModelException
  {
    int nodePosition = Integer.parseInt(event.getRequest().getNodePosition());
    StandardNode child = parent.newChild(parent.getNodeType().getNewChildNodeType(), nodePosition);
    this.repository.createStandardNode(child);
  }
  
  private void duplicateNode(StandardNode parent, StandardNodeCreationEvent event)
    throws RepositoryException, EventProcessingException, ModelException
  {
    long srcNodeId = Long.parseLong(event.getRequest().getSrcNodeId());
    StandardNode srcNode = this.repository.getStandardNode(event.getStandardId(), srcNodeId);
    duplicateNodeUnder(parent, srcNode);
  }
  
  private void duplicateChildren(StandardNode parent, StandardNodeCreationEvent event)
    throws RepositoryException, EventProcessingException, ModelException
  {
    long srcNodeId = Long.parseLong(event.getRequest().getSrcNodeId());
    StandardNode srcNode = this.repository.getStandardNode(event.getStandardId(), srcNodeId);
    for (StandardNode child : srcNode.getChildren()) {
      duplicateNodeUnder(parent, child);
    }
  }
  
  private void duplicateNodeUnder(StandardNode parent, StandardNode srcNode)
    throws ModelException, RepositoryException
  {
    int nodePosition = parent.getChildren().size();
    StandardNode copy = srcNode.deepCopy(srcNode.getStandard(), new StandardNodeCustomizer()
    {
      public void customize(StandardNode node)
        throws ModelException
      {}
    });
    copy.setNodePosition(nodePosition);
    parent.addChild(copy);
    this.repository.createStandardNode(copy);
  }
  
  public void processStandardNodeDeletion(StandardNodeDeleteEvent event)
    throws EventProcessingException
  {
    try
    {
      StandardNode node = this.repository.getStandardNode(event.getStandardId(), event.getNodeId());
      if (!node.getNodeType().allowDeletion()) {
        throw new EventProcessingException("Disallow manual deletion of node type: " + node.getNodeType().getNodeType());
      }
      this.repository.deleteNodeAndChildren(node);
    }
    catch (RepositoryException e)
    {
      throw new EventProcessingException(e);
    }
  }
  
  public void processStandardNodeMove(StandardNodeMoveEvent event)
    throws EventProcessingException
  {
    try
    {
        StandardNode node = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), event.getNodeId());
      if (!node.getNodeType().allowMove()) {
        throw new EventProcessingException("Disallow manual movement of node type: " + node.getNodeType().getNodeType());
      }
      if (node.getParentId() != event.getParentId())
      {
          StandardNode parent = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), event.getParentId());
        boolean validParent = false;
        for (StandardNodeType allowedParentType : node.getNodeType().getAllowedParentNodeTypes()) {
          if (parent.getNodeType().getNodeType().equals(allowedParentType.getNodeType()))
          {
            validParent = true;
            break;
          }
        }
        if (!validParent) {
          throw new EventProcessingException("Disallow move node type: " + node.getNodeType().getNodeType() + " to " + parent.getNodeType().getNodeType());
        }
        parent.addChild(node);
      }
      node.setNodePosition(((Integer)event.getNodePositions().get(Long.valueOf(node.getNodeId()))).intValue());
      this.repository.updateStandardNodeWithoutChildren(node);
      for (Map.Entry<Long, Integer> entry : event.getNodePositions().entrySet()) {
        if (((Long)entry.getKey()).longValue() != node.getNodeId())
        {
          StandardNode child = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), ((Long)entry.getKey()).longValue());
          if (child.getNodePosition() != ((Integer)entry.getValue()).intValue())
          {
            child.setNodePosition(((Integer)entry.getValue()).intValue());
            this.repository.updateStandardNodeWithoutChildren(child);
          }
        }
      }
    }
    catch (RepositoryException e)
    {
      StandardNode node;
      StandardNode parent;
      throw new EventProcessingException(e);
    }
    catch (ModelException e)
    {
      throw new EventProcessingException(e);
    }
  }
  
  public void processStandardNodePropertiesUpdate(StandardNodePropertiesUpdateEvent event)
    throws EventProcessingException
  {
    try
    {
      StringProperty nameProp = StringProperty.getPropertyOrNull(event.getUpdateRequest(), "name");
      if ((nameProp != null) && 
        (StringUtils.isEmpty(nameProp.getValue()))) {
        event.getUpdateRequest().removeProperty(nameProp.getName());
      }
      FloatProperty complianceProp = FloatProperty.getPropertyOrNull(event.getUpdateRequest(), "compliance");
      if (complianceProp != null)
      {
        double value = complianceProp.getValue();
        complianceProp.setValue(Math.min(100.0D, Math.max(value, 0.0D)));
      }
      if (event.getNodeId() == 0L)
      {
        Standard standard = this.repository.getStandardWithoutNodes(event.getStandardId());
        for (Property prop : event.getUpdateRequest().getProperties().values()) {
          standard.addProperty(prop);
        }
        this.repository.updateStandardWithoutChildren(standard);
      }
      else
      {
        StandardNode node = this.repository.getStandardNodeWithoutChildren(event.getStandardId(), event.getNodeId());
        event.getUpdateRequest().updateProperties(node, this.repository);
      }
    }
    catch (RepositoryException e)
    {
      throw new EventProcessingException(e);
    }
    catch (ModelException e)
    {
      throw new EventProcessingException(e);
    }
  }
}
