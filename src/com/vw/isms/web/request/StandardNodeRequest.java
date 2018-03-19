package com.vw.isms.web.request;

import java.util.List;

public class StandardNodeRequest {
    private String parentId;
    private List<String> children;
    private String nodePosition;
    private String text;
    private String srcNodeId;
    private boolean createNew;
    private boolean duplicate;
    private boolean appendChildren;

    public boolean isCreateNew() {
        return this.createNew;
    }

    public void setCreateNew(boolean createNew) {
        this.createNew = createNew;
    }

    public boolean isDuplicate() {
        return this.duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isAppendChildren() {
        return this.appendChildren;
    }

    public void setAppendChildren(boolean appendChildren) {
        this.appendChildren = appendChildren;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getChildren() {
        return this.children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public String getNodePosition() {
        return this.nodePosition;
    }

    public void setNodePosition(String nodePosition) {
        this.nodePosition = nodePosition;
    }

    public String getSrcNodeId() {
        return this.srcNodeId;
    }

    public void setSrcNodeId(String srcNodeId) {
        this.srcNodeId = srcNodeId;
    }
}
