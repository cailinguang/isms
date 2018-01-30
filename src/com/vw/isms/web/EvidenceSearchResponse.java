package com.vw.isms.web;

import com.vw.isms.standard.model.Evidence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvidenceSearchResponse {
    private final List<Evidence> results = new ArrayList();

    public boolean isSuccess() {
        return true;
    }

    public void add(Evidence e) {
        this.results.add(e);
    }

    public List<Evidence> getResults() {
        return Collections.unmodifiableList(this.results);
    }
}
