package com.excitel.service.admin;

import com.excitel.model.BroadbandPlan;

public interface BroadbandService {

    public BroadbandPlan addBroadbandPlan(BroadbandPlan broadbandPlan);
    public BroadbandPlan updateBroadbandPlan(BroadbandPlan broadbandPlan,String planId);
    public boolean deleteBroadbandPlan(String planId);


}
