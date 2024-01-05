package cumulocity.microservice.service.request.mgmt.service.c8y;

import com.cumulocity.sdk.client.Param;

public enum StatisticsParam implements Param{

    WITH_TOTAL_ELEMENTS("withTotalElements");
    
    private String paramName;

    StatisticsParam(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public String getName() {
        return paramName;
    }
    
}
