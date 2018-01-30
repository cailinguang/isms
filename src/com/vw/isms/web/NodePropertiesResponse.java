package com.vw.isms.web;

import com.vw.isms.property.Property;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.model.StandardNodeType;

import java.util.Map;

public class NodePropertiesResponse {
    private final String id;
    private final String uri;
    private final String type;
    private final StandardNode node;

    public NodePropertiesResponse(StandardNode node) {
        this.node = node;
        this.id = Long.toString(node.getNodeId());

        this.uri = ("/api/standards/" + node.getStandard().getStandardId() + "/nodes/" + node.getNodeId() + "/properties");
        this.type = node.getNodeType().getNodeType();
    }

    public String getId() {
        return this.id;
    }

    public String getUri() {
        return this.uri;
    }

    public String getType() {
        return this.type;
    }

    public Map<String, Property> getProperties() {
        return this.node.getProperties();
    }
}
