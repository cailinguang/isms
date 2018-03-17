package com.vw.isms.standard.model;

import java.util.Date;

/**
 * Created by clg on 2018/3/17.
 */
public class Login {
    private String userName;
    private Date lastLoginTime;
    private int loginCount;
    private String lastSixPassword;
    private Date lastChangePassTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String getLastSixPassword() {
        return lastSixPassword;
    }

    public void setLastSixPassword(String lastSixPassword) {
        this.lastSixPassword = lastSixPassword;
    }

    public Date getLastChangePassTime() {
        return lastChangePassTime;
    }

    public void setLastChangePassTime(Date lastChangePassTime) {
        this.lastChangePassTime = lastChangePassTime;
    }
}
