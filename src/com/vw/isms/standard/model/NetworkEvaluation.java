package com.vw.isms.standard.model;

/**
 * Created by clg on 2018/3/5.
 */
public class NetworkEvaluation {
    private Long evaluationId;
    private String evaluationTarget;
    private String evaluationIndex;
    private String controlItem;
    private String result;
    private String conformity;
    private String remark;
    private int order;


    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getEvaluationTarget() {
        return evaluationTarget;
    }

    public void setEvaluationTarget(String evaluationTarget) {
        this.evaluationTarget = evaluationTarget;
    }

    public String getEvaluationIndex() {
        return evaluationIndex;
    }

    public void setEvaluationIndex(String evaluationIndex) {
        this.evaluationIndex = evaluationIndex;
    }

    public String getControlItem() {
        return controlItem;
    }

    public void setControlItem(String controlItem) {
        this.controlItem = controlItem;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getConformity() {
        return conformity;
    }

    public void setConformity(String conformity) {
        this.conformity = conformity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
