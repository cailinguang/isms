package com.vw.isms.standard.repository;

import com.vw.isms.RepositoryException;
import com.vw.isms.property.BooleanProperty;
import com.vw.isms.property.EnumProperty;
import com.vw.isms.property.EvidenceSetProperty;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.Data;
import com.vw.isms.standard.model.*;
import com.vw.isms.web.request.*;

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

    PagingResult<Evidence> queryEvidenceTree(EvidenceSearchRequest search)
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

  DataClass queryDataClass(long classId);

  public void deleteDataType(String classType, Long classId);

    /**
     * 查询分类结果
     * @param query 条件
     * @return
     */
  public abstract List<DataClass> queryDataClass(DataClass query);

  public abstract void createDataMappingRelation(Long classId,Long dataId);

    int queryDataMappingSizeByClassId(Long classId);

  void deleteDataMappingRelation(Long classId, Long dataId);

    PagingResult<Data> queryDatasTree(EvidenceSearchRequest search)
            throws RepositoryException;

  PagingResult<Data> querySecuritiesTree(EvidenceSearchRequest search)
          throws RepositoryException;

  void createData(Data evidence)
          throws RepositoryException;

  void createSecurity(Data evidence)
          throws RepositoryException;

  Data getData(long evidenceId)
          throws RepositoryException;

  Data getSecurity(long evidenceId)
          throws RepositoryException;

  void updateData(Data evidence)
          throws RepositoryException;

  void updateSecurity(Data evidence)
          throws RepositoryException;

  void deleteData(long evidenceId)
          throws RepositoryException;

  void deleteSecurity(long evidenceId)
          throws RepositoryException;

  int queryDataCountByName(String name)
          throws RepositoryException;

  int querySecurityCountByName(String name)
          throws RepositoryException;

    List<String> queryNetworkSecurityTargets();

    PagingResult<Site> querySites(SiteSearchRequest search);

  Site querySiteById(Long siteId);

  void createSite(Site site);

  void updateSite(Site site);

  void deleteSite(Long siteId);

  List<NetworkEvaluation> queryNetworkSecurityByTarget(Long siteId,String target);

    void updateNetworkSecuritys(Long siteId,List<NetworkEvaluation> networkEvaluations);

    void importNetworkSecuritys(List<NetworkEvaluation> networkEvaluations);

    PagingResult<Dept> queryDepts(DeptRequest req);

    List<Dept> queryAllDept();

    void createDept(Dept dept);

    PagingResult<Role> queryRoles(RoleRequest search);

    List<Role> queryAllRoles();

    void createRole(Role role);

    void updateDept(Dept dept);

    void updateRole(Role role);

    Dept queryDeptByDeptId(String deptId);

    Role queryRoleByRoleId(String roleId);

    int countDeptByDeptName(String deptName);

    int countRoleByRoleName(String roleName);

    void deleteDeptByDeptId(String deptId);

    int countUserByDeptId(String deptId);

    PagingResult<Vulnerability> queryVulnerabilities(VulnerabilitySearchRequest search);

    Vulnerability queryVulnerability(String id);

    void createVulnerability(Vulnerability vulnerability);

    void deleteVulnerability(String id);

    void updateVulnerability(Vulnerability vulnerability);

    void deleteRoleByRoleId(String roleId);

    List<Menu> queryAllMenu();

    List<Menu> queryRoleMenuByRoleId(String roleId);

    void grantRoleMenu(String roleId, Long... menuIds);

    void createAuditLog(AuditLog auditLog);

    PagingResult<AuditLog> queryAuditLog(AuditSearchRequest search) throws Exception;

  List<Menu> queryUserMenu(String username);

    void exportDerby(String path);

    void deleteStandard(Long standardId);
}
