package com.vw.isms.web;

import com.vw.isms.standard.model.NetworkEvaluation;

import java.util.List;

/**
 * Created by clg on 2018/3/6.
 */
public class NetworkSecurityRequest {
    private List<NetworkEvaluation> networkEvaluations;

    public List<NetworkEvaluation> getNetworkEvaluations() {
        return networkEvaluations;
    }

    public void setNetworkEvaluations(List<NetworkEvaluation> networkEvaluations) {
        this.networkEvaluations = networkEvaluations;
    }
}
