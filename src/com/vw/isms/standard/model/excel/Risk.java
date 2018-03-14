package com.vw.isms.standard.model.excel;

import java.util.List;

/**
 * Created by clg on 2018/3/14.
 */
public class Risk {
    private String evaluationObject;//评估对象
    private String riskSource;//风险源
    private String consequence;//后果
    private String riskDescripion;//风险说明
    private String appendix;//附录A条款


    public String getEvaluationObject() {
        return evaluationObject;
    }

    public void setEvaluationObject(String evaluationObject) {
        this.evaluationObject = evaluationObject;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public void setRiskSource(String riskSource) {
        this.riskSource = riskSource;
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    public String getRiskDescripion() {
        return riskDescripion;
    }

    public void setRiskDescripion(String riskDescripion) {
        this.riskDescripion = riskDescripion;
    }

    public String getAppendix() {
        return appendix;
    }

    public void setAppendix(String appendix) {
        this.appendix = appendix;
    }

}
