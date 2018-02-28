package com.vw.isms.standard.model;

/**
 * Created by clg on 2018/2/28.
 * 数据分类
 */
public class DataClass {

    public static final String TYPE_EVIDENCE = "EVIDENCE";
    public static final String TYPE_DATA = "DATA";
    public static final String TYPE_INFORMATION_SECURITY = "INFORMATION_SECURITY";

    private long classId;
    private String classType; //分类类型：EVIDENCE/DATA/INFORMATION_SECURITY
    private long parentId;
    private String className;

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
