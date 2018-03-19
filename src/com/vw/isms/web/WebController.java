package com.vw.isms.web;

import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.*;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.standard.repository.UserRepository;
import com.vw.isms.util.PasswordUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private void setReadonlyStatus(ModelMap map, Authentication auth, boolean requestReadonly) {
        boolean readonly = false;
        if (!requestReadonly) {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ReadOnly"))
                    && !auth.getAuthorities().contains(new SimpleGrantedAuthority("PER_Admin"))){
                readonly = true;
            }
        }
        map.put("readonly", Boolean.valueOf(readonly));
    }

    private void setupAuth(ModelMap map, Authentication authentication) {
        map.put("auth", authentication);
        if(authentication!=null)
        map.put("menus",this.repository.queryUserMenu(authentication.getName()));
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
            map.put("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        } else {
            map.put("loginMessage", "");
        }
        return "login";
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);

        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }
        return error;
    }

    @RequestMapping({"admin"})
    public String admin(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        map.put("depts",this.repository.queryAllDept());
        map.put("roles",this.repository.queryAllRoles());
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
            map.put("error", "New password is not compliant to the security standard.<br/>" +
                    "1.密码至少同时包含下列四种字符：<br/>" +
                    "&nbsp;&nbsp;a）大写字母<br/>" +
                    "&nbsp;&nbsp;b）小写字母<br/>" +
                    "&nbsp;&nbsp;c）数字<br/>" +
                    "&nbsp;&nbsp;d）非字母数字字符（如！、@、#等）<br/>" +
                    "2.密码中不得包含登录用户名<br/>" +
                    "3.普通用户密码长度至少10位,管理员至少16位 ");
            return "reset_password";
        }
        if (!StringUtils.isEmpty(success)) {
            map.put("error", "New password has been set. Please login again.");
            return "reset_password";
        }

        if ((!StringUtils.isEmpty(oldPassword)) && (!StringUtils.isEmpty(newPassword))) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            if (!PasswordUtil.isCompliantPassword("admin".equals(user.getUsername())?PasswordUtil.adminUserLength:PasswordUtil.normalUserLength,user.getUsername(),newPassword)) {
                return "redirect:reset_password?noncompliant=true";
            }

            if (!this.userRepo.updatePassword(user.getUsername(), oldPassword, newPassword)) {
                return "redirect:reset_password?mismatch=true";
            }

            Login login = this.userRepo.queryUserLogin(user.getUsername());
            if(login!=null){
                String lastSixPass = login.getLastSixPassword();
                String[] sixPass = lastSixPass!=null?lastSixPass.split(","):new String[0];

                if(ArrayUtils.contains(sixPass,passwordEncoder.encode(newPassword))){
                    map.put("error", "New password must be different from the previous 6 passwords ");
                    return "reset_password";
                }

                sixPass = ArrayUtils.add(sixPass,passwordEncoder.encode(newPassword));
                if(sixPass.length>6){
                    sixPass = ArrayUtils.subarray(sixPass,sixPass.length-6,sixPass.length);
                }
                login.setLastSixPassword(org.apache.commons.lang3.StringUtils.join(sixPass,','));
                login.setLastChangePassTime(new Date());
                this.userRepo.updateUserLogin(login);

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
     * 网站管理
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"site"})
    public String getSite(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        map.put("depts",this.repository.queryAllDept());
        return "site";
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

    /**
     * 风险模块数据
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping({"risk_library"})
    public String getRisk(ModelMap map, Authentication authentication) {
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "risk_library";
    }


    /**
     * 角色管理
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("role")
    public String getRole(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "role";
    }

    /**
     * 审计日志
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("audit_log")
    public String getAuditLog(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "audit_log";
    }

    /**
     * 权限管理
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("permission")
    public String getPermission(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "permission";
    }

    /**
     * 权限管理
     * @param map
     * @param authentication
     * @return
     */
    @RequestMapping("backup")
    public String getBackup(ModelMap map, Authentication authentication){
        setupAuth(map, authentication);
        setReadonlyStatus(map, authentication, false);
        return "backup";
    }
}
