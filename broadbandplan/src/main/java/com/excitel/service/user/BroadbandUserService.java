package com.excitel.service.user;

import com.excitel.dto.RequestDTO;
import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;

import java.util.List;

public interface BroadbandUserService {
    List<BroadbandPlan> getBroadbandPlanWithQuery(RequestDTO params);
    BroadbandConnection createBroadbandConnection(BroadbandConnection broadbandConnection, String mobileNumber);
    BroadbandConnection getConnectionDetailsForUser(String mobileNumber, String status);
}
