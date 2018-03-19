package com.vw.isms.web;

import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.User;
import com.vw.isms.standard.repository.PagingResult;
import com.vw.isms.standard.repository.UserRepository;
import com.vw.isms.web.request.UserSearchRequest;
import com.vw.isms.web.request.AdminUserRequest;
import com.vw.isms.web.response.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @Autowired
    private UserRepository repository;

    @RequestMapping(value = {"/api/admin/users"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public PagingResult<User> queryUsers(@RequestBody UserSearchRequest req)
            throws RepositoryException {
        return this.repository.queryUser(req);
    }

    @RequestMapping(value = {"/api/admin/createuser"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public User createUser(@RequestBody AdminUserRequest req)
            throws RepositoryException {
        User user = new User();
        user.setUserName(req.getUserName());
        user.setDepartment(req.getDepartment());
        if (req.isReadonly()) {
            user.setRole("ReadOnly");
        } else {
            user.setRole("User");
        }
        if(this.repository.getUser(req.getUserName())!=null){
            throw new RepositoryException("userName is exists.");
        }

        String rawPassword = this.repository.createUser(user);
        user.setPassword(rawPassword);
        return user;
    }

    @RequestMapping(value = {"/api/admin/users/{userName}/password"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public User resetPassword(@PathVariable String userName)
            throws RepositoryException {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(this.repository.resetPassword(userName));
        return user;
    }

    @RequestMapping(value = {"/api/admin/users/{userName}/roles"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET}, produces = {"application/json"})
    @ResponseBody
    public Object getUserRoles(@PathVariable String userName){
        return this.repository.getUserRoles(userName);
    }

    @RequestMapping(value = {"/api/admin/users/{userName}/role"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST}, produces = {"application/json"})
    @ResponseBody
    public Object assignUserRole(@PathVariable String userName, @RequestBody AdminUserRequest req){
         this.repository.assignUserRole(userName,req.getRoles());
        return GenericResponse.success();
    }

    @RequestMapping(value = {"/api/admin/users/{userName}"}, method = {org.springframework.web.bind.annotation.RequestMethod.DELETE}, produces = {"application/json"})
    @ResponseBody
    public User deleteUser(@PathVariable String userName)
            throws RepositoryException {
        User user = this.repository.getUser(userName);
        if (user.getRole().equals("Admin")) {
            throw new RepositoryException("Cannot delete admin.");
        }
        this.repository.deleteUser(user);
        return user;
    }

    @RequestMapping(value = {"/api/admin/users/{userName}/profile"}, method = {org.springframework.web.bind.annotation.RequestMethod.PATCH}, produces = {"application/json"})
    @ResponseBody
    public User updateProfile(@PathVariable String userName, @RequestBody AdminUserRequest req)
            throws RepositoryException {
        User user = this.repository.getUser(userName);
        user.setRealName(req.getRealName());
        user.setEmail(req.getEmail());
        user.setTel(req.getTel());
        user.setDepartment(req.getDepartment());
        if (req.isReadonly()) {
            user.setRole("ReadOnly");
        } else {
            user.setRole("User");
        }
        this.repository.updateUser(user);
        return this.repository.getUser(userName);
    }
}
