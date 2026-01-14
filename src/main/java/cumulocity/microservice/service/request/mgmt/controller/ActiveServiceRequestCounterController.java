package cumulocity.microservice.service.request.mgmt.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.context.credentials.UserCredentials;

import cumulocity.microservice.service.request.mgmt.model.DeviceIds;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/service/request")
public class ActiveServiceRequestCounterController {
    
    private ServiceRequestUpdateService serviceRequestUpdateService;

    private ContextService<MicroserviceCredentials> contextService;

    @Autowired
    public ActiveServiceRequestCounterController(ServiceRequestUpdateService serviceRequestUpdateService, ContextService<MicroserviceCredentials> contextService) {
        this.serviceRequestUpdateService = serviceRequestUpdateService;
        this.contextService = contextService;
    }

    @Operation(summary = "Triggers an asynchronous REFRESH of the active service request counter (sr_ActiveStatus)", description = "Refreshes the active service request counter (sr_ActiveStatus) for the given managed object IDs.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "202", description = "Accepted"),})
	@PostMapping(path = "/device/sr_ActiveStatus/refresh",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> refresh(@Valid @RequestBody DeviceIds deviceIds) {
        // This triggers an asynchronous update of the service request counters for the provided managed object IDs. The endpoint doesn't wait for the update to complete.
        log.info("Received request to refresh sr_ActiveStatus for managedObjectIds: {}", deviceIds.getDeviceIds());
        serviceRequestUpdateService.refreshServiceRequestCounterForManagedObjects(deviceIds.getDeviceIds(), contextService.getContext());
        return ResponseEntity.accepted().build();
    }
}
