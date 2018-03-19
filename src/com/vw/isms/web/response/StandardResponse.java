package com.vw.isms.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vw.isms.ModelException;
import com.vw.isms.standard.model.JSTreeMetadata;
import com.vw.isms.standard.model.JSTreeNode;
import com.vw.isms.standard.model.Standard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponse {
    private final Standard standard;

    public StandardResponse(Standard standard) {
        this.standard = standard;
    }

    public boolean isSuccess() {
        return true;
    }

    public List<JSTreeNode> getData()
            throws ModelException {
        List<JSTreeNode> nodes = new ArrayList();
        this.standard.collectJSTreeNodes(nodes);
        return nodes;
    }

    public Map<String, JSTreeMetadata> getMetadata() {
        return this.standard.getJSTreeMetadata();
    }
}
