package com.vw.isms.web;

import com.vw.isms.ModelException;
import com.vw.isms.standard.model.JSPropertiesTreeNode;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;

public class PropertiesTreeResponse {
    private final JSPropertiesTreeNode resource;

    public PropertiesTreeResponse(StandardNode node)
            throws ModelException {
        this.resource = new JSPropertiesTreeNode(node.getNodeType().createEvaluationNode(node));
    }

    public PropertiesTreeResponse(Standard standard)
            throws ModelException {
        this.resource = new JSPropertiesTreeNode(standard.getRootNode().getNodeType().createEvaluationNode(standard.getRootNode()));
    }

    public boolean isSuccess() {
        return true;
    }

    public JSPropertiesTreeNode getResource() {
        return this.resource;
    }
}
