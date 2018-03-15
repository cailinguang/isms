package com.vw.isms.web;

/**
 * Created by clg on 2018/3/8.
 */
public class RoleRequest {
    private String roleName;
    private int pageNumber;
    private int itemPerPage;
    private Long[] menus;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public Long[] getMenus() {
        return menus;
    }

    public void setMenus(Long[] menus) {
        this.menus = menus;
    }
}
