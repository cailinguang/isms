package com.vw.isms.standard.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by clg on 2018/3/17.
 */
@JsonSerialize(using = CustomJsonSerial.class)
public class Site {
    private Long siteId;
    private String siteName;
    private String sitePath;
    private String siteUrl;
    private String siteGrade;
    private String siteDept;
    private String siteContacts;

    private String siteDeptName;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSitePath() {
        return sitePath;
    }

    public void setSitePath(String sitePath) {
        this.sitePath = sitePath;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteGrade() {
        return siteGrade;
    }

    public void setSiteGrade(String siteGrade) {
        this.siteGrade = siteGrade;
    }

    public String getSiteDept() {
        return siteDept;
    }

    public void setSiteDept(String siteDept) {
        this.siteDept = siteDept;
    }

    public String getSiteContacts() {
        return siteContacts;
    }

    public void setSiteContacts(String siteContacts) {
        this.siteContacts = siteContacts;
    }

    public String getSiteDeptName() {
        return siteDeptName;
    }

    public void setSiteDeptName(String siteDeptName) {
        this.siteDeptName = siteDeptName;
    }
}
