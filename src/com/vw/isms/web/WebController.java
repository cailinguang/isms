package com.vw.isms.web;

import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.Data;
import com.vw.isms.standard.model.Evaluation;
import com.vw.isms.standard.model.Evidence;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardType;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.standard.repository.UserRepository;
import com.vw.isms.util.PasswordUtil;

import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    @Autowired
    private StandardRepository repository;
    @Autowired
    private String basicInfoPath;
    @Autowired
    private UserRepository userRepo;

    private void setReadonlyStatus(ModelMap map, Authentication auth, boolean requestReadonly) {
        boolean readonly = true;
        if (!requestReadonly) {
            for (GrantedAuthority a : auth.getAuthorities()) {
                if (a.getAuthority().equals("ROLE_ADMIN")) {
                    readonly = false;
                    break;
                }
                if (a.getAuthority().equals("ROLE_USER")) {
                    readonly = false;
                    break;
                }
            }
        }
        map.put("readonly", Boolean.valueOf(readonly));
    }

    private void setupAuth(ModelMap map, Authentication authentication) {
        map.put("auth", authentication);
    }

    @RequestMapping({"basic_info"})
    public String getBasicInfo(ModelMap map, Authentication authentication)
            throws IOException {
        setupAuth(map, authentication);
        if (new File(this.basicInfoPath).exists()) {
            map.put("content", FileUtils.readFileToString(new File(this.basicInfoPath)));
        } else {
            map.put("content", this.basicInfoPath + " is missing.");
        }
        return "basic_info";
    }

    @RequestMapping({"standards"})
    public String getStandards(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        map.put("isEvaluation", Integer.valueOf(0));
        map.put("libraryUri", "/api/standard_library");
        setReadonlyStatus(map, authentication, false);
        return "standards";
    }

    @RequestMapping({"evaluations"})
    public String getEvaluations(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        map.put("isEvaluation", Integer.valueOf(1));
        map.put("libraryUri", "/api/evaluation_library");
        setReadonlyStatus(map, authentication, false);
        return "standards";
    }

    @RequestMapping({"evidences"})
    public String getEvidences(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "evidence_library";
    }

    @RequestMapping({"create_standard"})
    public String createStandard(@RequestParam("type") String type, ModelMap map, Authentication authentication)
            throws ModelException, RepositoryException {
        setupAuth(map, authentication);
        Standard standard = StandardType.getInstance(type).createTemplate();
        this.repository.createStandard(standard);
        return "redirect:edit_standard?id=" + standard.getStandardId();
    }

    @RequestMapping({"edit_standard"})
    public String editStandard(@RequestParam("id") String standardId, @RequestParam(value = "readonly", required = false) boolean readonly, ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        map.put("standardId", standardId);
        setReadonlyStatus(map, authentication, readonly);
        return "edit_standard";
    }

    @RequestMapping({"create_evaluation"})
    public String createEvaluation(@RequestParam("id") Long standardId, ModelMap map, Authentication authentication)
            throws RepositoryException, ModelException {
        setupAuth(map, authentication);
        Standard standard = this.repository.getStandard(standardId.longValue());
        Standard evaluation = standard.forkEvaluation();
        this.repository.createStandard(evaluation);
        return "redirect:edit_evaluation?id=" + evaluation.getStandardId();
    }

    @RequestMapping({"edit_evaluation"})
    public String editEvaluation(@RequestParam("id") Long standardId, @RequestParam(value = "readonly", required = false) boolean readonly, ModelMap map, Authentication authentication)
            throws RepositoryException, ModelException {
        setupAuth(map, authentication);
        Standard standard = this.repository.getStandard(standardId.longValue());
        map.put("standardId", Long.toString(standard.getStandardId()));
        setReadonlyStatus(map, authentication, readonly);
        return "edit_evaluation";
    }

    @RequestMapping({"score_evaluation"})
    public String scoreEvaluation(@RequestParam("id") Long standardId, ModelMap map, Authentication authentication)
            throws RepositoryException, ModelException {
        setupAuth(map, authentication);
        Standard standard = this.repository.getStandard(standardId.longValue());
        Evaluation evaluation = standard.getStandardType().createEvaluation(standard);
        map.put("evaluation", evaluation);
        map.put("maxScore", Float.valueOf(evaluation.getMaxScore()));
        return "score_evaluation";
    }

    @RequestMapping(value = {"download_evidence/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<FileSystemResource> downloadEvidence(@PathVariable("id") long id, ModelMap map, Authentication authentication)
            throws RepositoryException {
        setupAuth(map, authentication);
        Evidence ev = this.repository.getEvidence(id);
        MediaType mediaType = MediaType.valueOf(ev.getContentType());
        FileSystemResource res = new FileSystemResource(new File(this.repository.getEvidencePath(ev.getPath())));
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(res);
    }

    @RequestMapping({"login"})
    public String login(HttpServletRequest request, ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        if (request.getParameter("error") != null) {
            map.put("loginMessage", "Invalid user name or password.");
        } else {
            map.put("loginMessage", "");
        }
        return "login";
    }

    @RequestMapping({"admin"})
    public String admin(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        return "admin";
    }

    @RequestMapping({"reset_password"})
    public String resetPassword(@RequestParam(value = "oldPassword", required = false) String oldPassword, @RequestParam(value = "newPassword", required = false) String newPassword, @RequestParam(value = "mismatch", required = false) String mismatch, @RequestParam(value = "noncompliant", required = false) String noncompliant, @RequestParam(value = "success", required = false) String success, ModelMap map, Authentication authentication)
            throws RepositoryException {
        setupAuth(map, authentication);
        if (!StringUtils.isEmpty(mismatch)) {
            map.put("error", "Old password is wrong.");
            return "reset_password";
        }
        if (!StringUtils.isEmpty(noncompliant)) {
            map.put("error", "New password is not compliant to the security standard.");
            return "reset_password";
        }
        if (!StringUtils.isEmpty(success)) {
            map.put("error", "New password has been set.");
            return "reset_password";
        }
        if ((!StringUtils.isEmpty(oldPassword)) && (!StringUtils.isEmpty(newPassword))) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            if (!PasswordUtil.isCompliantPassword(newPassword)) {
                return "redirect:reset_password?noncompliant=true";
            }
            if (!this.userRepo.updatePassword(user.getUsername(), oldPassword, newPassword)) {
                return "redirect:reset_password?mismatch=true";
            }
            return "redirect:reset_password?success=true";
        }
        return "reset_password";
    }

    /**
     * 证据库页面
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"evidences_tree"})
    public String getEvidencesTree(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "evidence_tree_library";
    }

    /**
     * 数据分级分类页面
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"data_tree"})
    public String getDataTree(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "data_tree_library";
    }

    /**
     * 下载数据
     * @param id
     * @param map
     * @param authentication
     * @return
     * @throws RepositoryException
     */
    @RequestMapping(value = {"download_data/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<FileSystemResource> downloadData(@PathVariable("id") long id, ModelMap map, Authentication authentication)
            throws RepositoryException {
        setupAuth(map, authentication);
        Data ev = this.repository.getData(id);
        MediaType mediaType = MediaType.valueOf(ev.getContentType());
        FileSystemResource res = new FileSystemResource(new File(this.repository.getEvidencePath(ev.getPath())));
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(res);
    }

    /**
     * 信息安全对标页面
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"security_tree"})
    public String getSecurityTree(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "security_tree_library";
    }

    /**
     * 下载信息安全对标
     * @param id
     * @param map
     * @param authentication
     * @return
     * @throws RepositoryException
     */
    @RequestMapping(value = {"download_security/{id}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<FileSystemResource> downloadSecurity(@PathVariable("id") long id, ModelMap map, Authentication authentication)
            throws RepositoryException {
        setupAuth(map, authentication);
        Data ev = this.repository.getSecurity(id);
        MediaType mediaType = MediaType.valueOf(ev.getContentType());
        FileSystemResource res = new FileSystemResource(new File(this.repository.getEvidencePath(ev.getPath())));
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(res);
    }


    /**
     * 网络安全法
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"network_security"})
    public String getNetworkSecurity(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "network_security";
    }

    /**
     * view网络安全法
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"view_security"})
    public String getViewSecurity(ModelMap map, Authentication authentication,@RequestParam String target) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        map.put("target",target);
        return "view_security";
    }

    /**
     * 部门管理
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("dept")
    public String getDept(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "dept";
    }

    /**
     * 漏洞模块数据
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("vulnerability")
    public String getVulnerability(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "vulnerability";
    }

}
