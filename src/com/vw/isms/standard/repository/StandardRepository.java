package com.vw.isms.standard.repository;

import com.vw.isms.RepositoryException;
import com.vw.isms.property.BooleanProperty;
import com.vw.isms.property.EnumProperty;
import com.vw.isms.property.EvidenceSetProperty;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.DataClass;
import com.vw.isms.standard.model.Evidence;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;

import java.util.List;

public abstract interface StandardRepository
{
  public abstract void createStandard(Standard paramStandard)
    throws RepositoryException;
  
  public abstract void updateStandardWithoutChildren(Standard paramStandard)
    throws RepositoryException;
  
  public abstract Standard getStandardWithoutNodes(long paramLong)
    throws RepositoryException;
  
  public abstract Standard getStandard(long paramLong)
    throws RepositoryException;
  
  public abstract void deleteStandardAndChildren(long paramLong)
    throws RepositoryException;
  
  public abstract void createStandardNode(StandardNode paramStandardNode)
    throws RepositoryException;
  
  public abstract void updateStandardNodeWithoutChildren(StandardNode paramStandardNode)
    throws RepositoryException;
  
  public abstract StandardNode getStandardNodeWithoutChildren(long paramLong1, long paramLong2)
    throws RepositoryException;
  
  public abstract StandardNode getStandardNode(long paramLong1, long paramLong2)
    throws RepositoryException;
  
  public abstract void deleteNodeAndChildren(StandardNode paramStandardNode)
    throws RepositoryException;
  
  public abstract void updateBooleanProperty(StandardNode paramStandardNode, BooleanProperty paramBooleanProperty)
    throws RepositoryException;
  
  public abstract void updateEnumProperty(StandardNode paramStandardNode, EnumProperty paramEnumProperty)
    throws RepositoryException;
  
  public abstract void updateEvidenceSetProperty(StandardNode paramStandardNode, EvidenceSetProperty paramEvidenceSetProperty)
    throws RepositoryException;
  
  public abstract void updateFloatProperty(StandardNode paramStandardNode, FloatProperty paramFloatProperty)
    throws RepositoryException;
  
  public abstract void updateStringProperty(StandardNode paramStandardNode, StringProperty paramStringProperty)
    throws RepositoryException;
  
  public abstract void createBooleanProperty(StandardNode paramStandardNode, BooleanProperty paramBooleanProperty)
    throws RepositoryException;
  
  public abstract void createEnumProperty(StandardNode paramStandardNode, EnumProperty paramEnumProperty)
    throws RepositoryException;
  
  public abstract void createEvidenceSetProperty(StandardNode paramStandardNode, EvidenceSetProperty paramEvidenceSetProperty)
    throws RepositoryException;
  
  public abstract void createFloatProperty(StandardNode paramStandardNode, FloatProperty paramFloatProperty)
    throws RepositoryException;
  
  public abstract void createStringProperty(StandardNode paramStandardNode, StringProperty paramStringProperty)
    throws RepositoryException;
  
  public abstract PagingResult<Standard> queryStandard(StandardSearchRequest paramStandardSearchRequest, boolean paramBoolean)
    throws RepositoryException;
  
  public abstract PagingResult<Evidence> queryEvidence(EvidenceSearchRequest paramEvidenceSearchRequest)
    throws RepositoryException;
  
  public abstract Evidence getEvidence(long paramLong)
    throws RepositoryException;
  
  public abstract PagingResult<Evidence> queryEvidenceByName(String paramString)
    throws RepositoryException;
  
  public abstract void createEvidence(Evidence paramEvidence)
    throws RepositoryException;
  
  public abstract void updateEvidence(Evidence paramEvidence)
    throws RepositoryException;
  
  public abstract String getEvidencePath(String paramString);
  
  public abstract void deleteEvidence(long paramLong)
    throws RepositoryException;

    /**
     * 创建数据分类
     * @param dataClass
     */
  public abstract void createDataType(DataClass dataClass);

    void updateDataType(DataClass dataClass);

    /**
     * 查询分类结果
     * @param query 条件
     * @return
     */
  public abstract List<DataClass> queryDataClass(DataClass query);
}
