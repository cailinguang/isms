package com.vw.isms.standard.model.excel;

import java.util.List;

/**
 * Created by clg on 2018/3/17.
 */
public class RiskCategory {
    private String name;
    private List<Risk> risks = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }
}
