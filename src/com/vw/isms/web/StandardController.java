package com.vw.isms.web;

import com.vw.isms.EventProcessingException;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.property.Properties;
import com.vw.isms.standard.model.Data;
import com.vw.isms.standard.event.StandardEventProcessor;
import com.vw.isms.standard.event.StandardNodeCreationEvent;
import com.vw.isms.standard.event.StandardNodeDeleteEvent;
import com.vw.isms.standard.event.StandardNodeMoveEvent;
import com.vw.isms.standard.event.StandardNodePropertiesUpdateEvent;
import com.vw.isms.standard.event.StandardNodeRenameEvent;
import com.vw.isms.standard.model.*;
import com.vw.isms.standard.repository.EvidenceSearchRequest;
import com.vw.isms.standard.repository.PagingResult;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.standard.repository.StandardSearchRequest;
import com.vw.isms.util.IdUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class StandardController {
    @Autowired
    private StandardRepository repository;

    private StandardEventProcessor getProcessor() {
        return new StandardEventProcessor(this.repository);
    }

    @RequestMapping(value = {"/api/standard_library"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<StandardSearchResult> queryStandads(@RequestBody StandardSearchRequest req)
            throws EventProcessingException {
        return queryStandardsOrEvaluations(req, false);
    }

    @RequestMapping(value = {"/api/evaluation_library"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<StandardSearchResult> queryEvaluations(@RequestBody StandardSearchRequest req)
            throws EventProcessingException {
        return queryStandardsOrEvaluations(req, true);
    }

    private PagingResult<StandardSearchResult> queryStandardsOrEvaluations(StandardSearchRequest req, boolean isEvaluation)
            throws EventProcessingException {
        try {
            PagingResult<Standard> result = this.repository.queryStandard(req, isEvaluation);

            PagingResult<StandardSearchResult> resp = new PagingResult(result.getPageNumber(), result.getItemPerPage());
            resp.setHasNextPage(result.isHasNextPage());
            for (Standard standard : result.getResults()) {
                resp.add(new StandardSearchResult(standard));
            }
            return resp;
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /*@RequestMapping(value = {"/api/properties/evidences"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<Evidence> queryEvidences(@RequestBody EvidenceSearchRequest req)
            throws EventProcessingException {
        try {
            return this.repository.queryEvidence(req);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }*/

    @RequestMapping(value = {"/api/properties/evidences/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateEvidences(@PathVariable Long evidenceId, @RequestBody Evidence ev, HttpServletRequest request)
            throws EventProcessingException {
        try {
            Evidence src = this.repository.getEvidence(evidenceId.longValue());
            src.setDescription(ev.getDescription());
            this.repository.updateEvidence(src);
            this.repository.createDataMappingRelation(ev.getClassId(),evidenceId);
            return GenericResponse.success();
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    @RequestMapping(value = {"/api/standards/{standardId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public StandardResponse getStandardTree(@PathVariable Long standardId)
            throws EventProcessingException {
        return createStandardResponse(standardId);
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public StandardResponse createNode(@PathVariable Long standardId, @RequestBody StandardNodeRequest req)
            throws EventProcessingException {
        StandardNodeCreationEvent event = new StandardNodeCreationEvent(standardId.longValue(), req);
        event.process(getProcessor());
        return createStandardResponse(standardId);
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public StandardResponse updateNode(@PathVariable Long standardId, @PathVariable Long id, @RequestBody StandardNodeRequest req)
            throws EventProcessingException {
        if (!StringUtils.isEmpty(req.getParentId())) {
            StandardNodeMoveEvent event = new StandardNodeMoveEvent(standardId.longValue(), Long.parseLong(req.getParentId()), id.longValue(), req.getChildren());
            event.process(getProcessor());
        } else {
            StandardNodeRenameEvent event = new StandardNodeRenameEvent(standardId.longValue(), id.longValue(), req.getText());
            event.process(getProcessor());
        }
        return createStandardResponse(standardId);
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public StandardResponse deleteNode(@PathVariable Long standardId, @PathVariable Long id)
            throws EventProcessingException {
        StandardNodeDeleteEvent event = new StandardNodeDeleteEvent(standardId.longValue(), id.longValue());
        event.process(getProcessor());
        return createStandardResponse(standardId);
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes/{nodeId}/properties"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public PropertiesTreeResponse getPropertiesTree(@PathVariable Long standardId, @PathVariable Long nodeId)
            throws EventProcessingException {
        return constructPropertiesTree(standardId, nodeId);
    }

    private PropertiesTreeResponse constructPropertiesTree(Long standardId, Long nodeId)
            throws EventProcessingException {
        try {
            if (nodeId.longValue() == 0L) {
                Standard standard = this.repository.getStandard(standardId.longValue());
                return new PropertiesTreeResponse(standard);
            }
            StandardNode node = this.repository.getStandardNode(standardId.longValue(), nodeId.longValue());
            return new PropertiesTreeResponse(node);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        } catch (ModelException e) {
            throw new EventProcessingException(e);
        }
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes/{nodeId}/nodeProperties"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public NodePropertiesResponse getNodeProperties(@PathVariable Long standardId, @PathVariable Long nodeId)
            throws EventProcessingException {
        try {
            if (nodeId.longValue() == 0L) {
                Standard standard = this.repository.getStandardWithoutNodes(standardId.longValue());
                return new NodePropertiesResponse(standard.getRootNode());
            }
            StandardNode node = this.repository.getStandardNodeWithoutChildren(standardId.longValue(), nodeId.longValue());
            return new NodePropertiesResponse(node);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        } catch (ModelException e) {
            throw new EventProcessingException(e);
        }
    }

    @RequestMapping(value = {"/api/standards/{standardId}/nodes/{nodeId}/properties"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateNodeProperties(@PathVariable Long standardId, @PathVariable Long nodeId, @RequestBody Properties props)
            throws EventProcessingException {
        StandardNodePropertiesUpdateEvent event = new StandardNodePropertiesUpdateEvent(standardId.longValue(), nodeId.longValue(), props);
        event.process(getProcessor());
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/standards/{standardId}/archive"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateStandardArchive(@PathVariable Long standardId, @RequestBody UpdateArchiveStatusRequest req)
            throws RepositoryException {
        Standard standard = this.repository.getStandardWithoutNodes(standardId.longValue());
        standard.setArchived(req.isArchived());
        this.repository.updateStandardWithoutChildren(standard);
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/properties/evidences/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse deleteEvidence(@PathVariable Long evidenceId,@RequestBody EvidenceSearchRequest req)
            throws RepositoryException, IOException {
        Evidence src = this.repository.getEvidence(evidenceId.longValue());
        String absPath = this.repository.getEvidencePath(src.getPath());
        this.repository.deleteEvidence(evidenceId.longValue());
        this.repository.deleteDataMappingRelation(req.getClassId(),evidenceId);
        FileUtils.forceDelete(new File(absPath));
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/upload_evidence"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Evidence uploadEvidence(MultipartHttpServletRequest request, HttpServletResponse response)
            throws EventProcessingException, RepositoryException, IOException {
        Iterator<String> iter = request.getFileNames();
        if (!iter.hasNext()) {
            throw new EventProcessingException("No file uploaded.");
        }
        MultipartFile mpf = request.getFile((String) iter.next());
        if (StringUtils.isEmpty(mpf.getOriginalFilename())) {
            throw new EventProcessingException("No file uploaded.");
        }

        String classId = request.getParameter("classId");
        if(classId==null||classId.equals("")){
            throw new EventProcessingException("No evidence directory selected.");
        }

        if (!this.repository.queryEvidenceByName(FilenameUtils.getName(mpf.getOriginalFilename())).getResults().isEmpty()) {
            throw new EventProcessingException("A file of the same name already exists.");
        }


        String path = UUID.randomUUID().toString();
        String absPath = this.repository.getEvidencePath(path);
        FileCopyUtils.copy(mpf.getBytes(), new File(absPath));
        Evidence ev = new Evidence();
        ev.setId(IdUtil.next());
        ev.setName(FilenameUtils.getName(mpf.getOriginalFilename()));
        ev.setDescription(request.getParameter("description"));
        ev.setPath(path);
        ev.setContentType(mpf.getContentType());
        this.repository.createEvidence(ev);
        this.repository.createDataMappingRelation(Long.parseLong(classId),ev.getId());
        return ev;
    }

    private StandardResponse createStandardResponse(Long standardId)
            throws EventProcessingException {
        try {
            Standard standard = this.repository.getStandard(standardId.longValue());
            return new StandardResponse(standard);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 数据分类树数据
     * @param classType
     * @return
     */
    @RequestMapping(value = {"/api/data_type/{classType}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public Object dataTypeAll(@PathVariable String classType){
        DataClass query = new DataClass();
        query.setClassType(classType);
        return this.repository.queryDataClass(query);
    }

    private DataClass createDataClassFromReq(StandardNodeRequest req,String classType){
        DataClass dataClass = new DataClass();
        dataClass.setPosition(Integer.parseInt(req.getNodePosition()));
        dataClass.setParentId(Long.parseLong(req.getParentId()));
        dataClass.setClassType(classType);
        dataClass.setClassName(req.getText());
        return dataClass;
    }

    /**
     * 新增数据分类
     * @param classType
     * @param req
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/data_type/{classType}/nodes"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public Object createDataClass(@PathVariable String classType, @RequestBody StandardNodeRequest req)
            throws EventProcessingException {
        DataClass dataClass = createDataClassFromReq(req,classType);
        dataClass.setClassId(IdUtil.next());
        this.repository.createDataType(dataClass);
        return dataTypeAll(classType);
    }

    /**
     * 更新数据分类
     * @param classType
     * @param id
     * @param req
     * @param move
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/data_type/{classType}/nodes/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public Object updateDataClass(@PathVariable String classType, @PathVariable Long id, @RequestBody StandardNodeRequest req,String move)
            throws EventProcessingException {
        if(req.getChildren()!=null){
            for(String key:req.getChildren()){
                DataClass children = this.repository.queryDataClass(Long.parseLong(key));
                children.setParentId(Long.parseLong(req.getParentId()));
                this.repository.updateDataType(children);
            }
        }else{
            DataClass dataClass = createDataClassFromReq(req,classType);
            dataClass.setClassId(id);
            this.repository.updateDataType(dataClass);
        }
        return dataTypeAll(classType);
    }

    /**
     * 删除数据分类
     * @param classType
     * @param id
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/data_type/{classType}/nodes/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public Object deleteDataClass(@PathVariable String classType, @PathVariable Long id)
            throws EventProcessingException {
        this.repository.deleteDataType(classType,id);
        return dataTypeAll(classType);
    }

    /**
     * 查询证据,根据数据分类id查询
     * @param req
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/properties/evidences"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<Evidence> queryTreeEvidences(@RequestBody EvidenceSearchRequest req)
            throws EventProcessingException {
        try {
            return this.repository.queryEvidenceTree(req);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 查询数据分类分级,根据数据分类id查询
     * @param req
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/properties/datas"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<Data> queryTreeDatas(@RequestBody EvidenceSearchRequest req)
            throws EventProcessingException {
        try {
            return this.repository.queryDatasTree(req);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 上传数据分类分级
     * @param request
     * @param response
     * @return
     * @throws EventProcessingException
     * @throws RepositoryException
     * @throws IOException
     */
    @RequestMapping(value = {"/api/upload_data"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Data uploadData(MultipartHttpServletRequest request, HttpServletResponse response,Authentication authentication)
            throws EventProcessingException, RepositoryException, IOException {
        Iterator<String> iter = request.getFileNames();
        if (!iter.hasNext()) {
            throw new EventProcessingException("No file uploaded.");
        }
        MultipartFile mpf = request.getFile((String) iter.next());
        if (StringUtils.isEmpty(mpf.getOriginalFilename())) {
            throw new EventProcessingException("No file uploaded.");
        }

        String classId = request.getParameter("classId");
        if(classId==null||classId.equals("")){
            throw new EventProcessingException("No evidence directory selected.");
        }

        if (this.repository.queryDataCountByName(FilenameUtils.getName(mpf.getOriginalFilename()))>0) {
            throw new EventProcessingException("A file of the same name already exists.");
        }


        String path = UUID.randomUUID().toString();
        String absPath = this.repository.getEvidencePath(path);
        FileCopyUtils.copy(mpf.getBytes(), new File(absPath));
        Data ev = new Data();
        ev.setId(IdUtil.next());
        ev.setName(FilenameUtils.getName(mpf.getOriginalFilename()));
        ev.setDescription(request.getParameter("description"));
        ev.setPath(path);
        ev.setContentType(mpf.getContentType());
        ev.setUserName(authentication.getName());
        this.repository.createData(ev);
        this.repository.createDataMappingRelation(Long.parseLong(classId),ev.getId());
        return ev;
    }

    /**
     * 更新数据分级分类
     * @param evidenceId
     * @param ev
     * @param request
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/properties/data/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateData(@PathVariable Long evidenceId, @RequestBody Data ev, HttpServletRequest request)
            throws EventProcessingException {
        try {
            Data src = this.repository.getData(evidenceId.longValue());
            src.setDescription(ev.getDescription());
            this.repository.updateData(src);
            this.repository.createDataMappingRelation(ev.getClassId(),evidenceId);
            return GenericResponse.success();
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 删除数据分级分类
     * @param evidenceId
     * @param req
     * @return
     * @throws RepositoryException
     * @throws IOException
     */
    @RequestMapping(value = {"/api/properties/data/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse deleteData(@PathVariable Long evidenceId,@RequestBody EvidenceSearchRequest req)
            throws RepositoryException, IOException {
        Data src = this.repository.getData(evidenceId.longValue());
        String absPath = this.repository.getEvidencePath(src.getPath());
        this.repository.deleteData(evidenceId.longValue());
        this.repository.deleteDataMappingRelation(req.getClassId(),evidenceId);
        FileUtils.forceDelete(new File(absPath));
        return GenericResponse.success();
    }

    /**
     * 查询信息安全对标,根据数据分类id查询
     * @param req
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/properties/securities"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<Data> queryTreeSecurities(@RequestBody EvidenceSearchRequest req)
            throws EventProcessingException {
        try {
            return this.repository.querySecuritiesTree(req);
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 上传信息安全对标
     * @param request
     * @param response
     * @return
     * @throws EventProcessingException
     * @throws RepositoryException
     * @throws IOException
     */
    @RequestMapping(value = {"/api/upload_security"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Data uploadSecurity(MultipartHttpServletRequest request, HttpServletResponse response,Authentication authentication)
            throws EventProcessingException, RepositoryException, IOException {
        Iterator<String> iter = request.getFileNames();
        if (!iter.hasNext()) {
            throw new EventProcessingException("No file uploaded.");
        }
        MultipartFile mpf = request.getFile((String) iter.next());
        if (StringUtils.isEmpty(mpf.getOriginalFilename())) {
            throw new EventProcessingException("No file uploaded.");
        }

        String classId = request.getParameter("classId");
        if(classId==null||classId.equals("")){
            throw new EventProcessingException("No evidence directory selected.");
        }

        if (this.repository.querySecurityCountByName(FilenameUtils.getName(mpf.getOriginalFilename()))>0) {
            throw new EventProcessingException("A file of the same name already exists.");
        }


        String path = UUID.randomUUID().toString();
        String absPath = this.repository.getEvidencePath(path);
        FileCopyUtils.copy(mpf.getBytes(), new File(absPath));
        Data ev = new Data();
        ev.setId(IdUtil.next());
        ev.setName(FilenameUtils.getName(mpf.getOriginalFilename()));
        ev.setDescription(request.getParameter("description"));
        ev.setPath(path);
        ev.setContentType(mpf.getContentType());
        ev.setUserName(authentication.getName());
        this.repository.createSecurity(ev);
        this.repository.createDataMappingRelation(Long.parseLong(classId),ev.getId());
        return ev;
    }

    /**
     * 更新信息安全对标
     * @param evidenceId
     * @param ev
     * @param request
     * @return
     * @throws EventProcessingException
     */
    @RequestMapping(value = {"/api/properties/security/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateSecurity(@PathVariable Long evidenceId, @RequestBody Evidence ev, HttpServletRequest request)
            throws EventProcessingException {
        try {
            Data src = this.repository.getSecurity(evidenceId.longValue());
            src.setDescription(ev.getDescription());
            this.repository.updateSecurity(src);
            this.repository.createDataMappingRelation(ev.getClassId(),evidenceId);
            return GenericResponse.success();
        } catch (RepositoryException e) {
            throw new EventProcessingException(e);
        }
    }

    /**
     * 删除信息安全对标
     * @param evidenceId
     * @param req
     * @return
     * @throws RepositoryException
     * @throws IOException
     */
    @RequestMapping(value = {"/api/properties/security/{evidenceId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse deleteSecurity(@PathVariable Long evidenceId,@RequestBody EvidenceSearchRequest req)
            throws RepositoryException, IOException {
        Data src = this.repository.getSecurity(evidenceId.longValue());
        String absPath = this.repository.getEvidencePath(src.getPath());
        this.repository.deleteSecurity(evidenceId.longValue());
        this.repository.deleteDataMappingRelation(req.getClassId(),evidenceId);
        FileUtils.forceDelete(new File(absPath));
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/properties/network-security/"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public Object queryNetworkSecurityTarget() throws RepositoryException, IOException {
        return this.repository.queryNetworkSecurityTargets();
    }

    @RequestMapping(value = {"/api/properties/network-security/{target}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public Object queryTargetInfos(@PathVariable String target) throws RepositoryException, IOException {
        return this.repository.queryNetworkSecurityByTarget(target);
    }

    @RequestMapping(value = {"/api/properties/network-security/{target}"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public GenericResponse updateTargetInfos(@PathVariable String target,@RequestBody NetworkSecurityRequest req) throws RepositoryException, IOException {
        this.repository.updateNetworkSecuritys(req.getNetworkEvaluations());
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/properties/network-security/{target}/import"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public GenericResponse importNetworkSecurity(@PathVariable String target,MultipartHttpServletRequest request) throws Exception {
        Iterator<String> iter = request.getFileNames();
        if (!iter.hasNext()) {
            throw new EventProcessingException("No file uploaded.");
        }
        MultipartFile mpf = request.getFile((String) iter.next());
        if (StringUtils.isEmpty(mpf.getOriginalFilename())) {
            throw new EventProcessingException("No file uploaded.");
        }
        List<NetworkEvaluation> results = ResolveExcel.resolveNetrowkEvaluation(mpf.getInputStream());
        if(results==null||results.size()==0){
            throw new EventProcessingException("no item find from excel.");
        }
        this.repository.importNetworkSecuritys(results);

        return GenericResponse.success();
    }


    @RequestMapping(value = {"/api/depts"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public Object getDepts(@RequestBody DeptRequest req){
        return this.repository.queryDepts(req);
    }

    @RequestMapping(value = {"/api/dept"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public Object createDept(@RequestBody Dept dept) throws Exception{
        if(this.repository.queryDeptByDeptId(dept.getDeptId())!=null){
            throw new EventProcessingException("DepartmentID already exist.");
        }
        if(this.repository.countDeptByDeptName(dept.getDeptName())!=0){
            throw new EventProcessingException("DepartmentName already exist.");
        }

        this.repository.createDept(dept);
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/dept/{deptId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public Object getDept(@PathVariable String deptId){
        return this.repository.queryDeptByDeptId(deptId);
    }

    @RequestMapping(value = {"/api/dept/{deptId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public Object updateDept(@PathVariable String deptId,@RequestBody Dept dept) throws Exception{
        Dept oldDept = this.repository.queryDeptByDeptId(deptId);

        if(!oldDept.getDeptName().equals(dept.getDeptName()) && this.repository.countDeptByDeptName(dept.getDeptName())!=0){
            throw new EventProcessingException("DepartmentName already exist.");
        }
        dept.setDeptId(deptId);
        this.repository.updateDept(dept);
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/dept/{deptId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public Object deleteDept(@PathVariable String deptId){
        this.repository.deleteDeptByDeptId(deptId);
        return GenericResponse.success();
    }

    @RequestMapping(value="/api/vulnerabilities",method = RequestMethod.GET)
    public Object getVulnerabilities(String system){
        return null;
    }
}
