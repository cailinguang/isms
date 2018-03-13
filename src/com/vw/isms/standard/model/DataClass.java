package com.vw.isms.standard.model;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Created by clg on 2018/2/28.
 * 数据分类
 */
@JsonSerialize(using = CustomJsonSerial.class)
public class DataClass {

    public static final String TYPE_EVIDENCE = "EVIDENCE";
    public static final String TYPE_DATA = "DATA";
    public static final String TYPE_INFORMATION_SECURITY = "SECURITY";

    private long classId;
    private String classType; //分类类型：EVIDENCE/DATA/INFORMATION_SECURITY
    private Long parentId;
    private String className;
    private int position;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

