package com.vw.isms.standard.repository;

/**
 * Created by clg on 2018/3/17.
 */
public class SiteSearchRequest {
    private String siteName;
    private int pageNumber;
    private int itemPerPage;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }
}
