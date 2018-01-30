package com.vw.isms.web;

import com.vw.isms.ModelException;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.Standard;
import com.vw.isms.standard.model.StandardType;

public class StandardSearchResult {
    private final Standard standard;

    public StandardSearchResult(Standard standard) {
        this.standard = standard;
    }

    public String getStandardId() {
        return Long.toString(this.standard.getStandardId());
    }

    public String getName()
            throws ModelException {
        return StringProperty.getProperty(this.standard, "name").getValue();
    }

    public String getStandardType() {
        return this.standard.getStandardType().getType();
    }

    public boolean isArchived() {
        return this.standard.isArchived();
    }
}
