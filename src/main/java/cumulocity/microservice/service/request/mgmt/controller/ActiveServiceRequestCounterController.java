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

import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/service/request")
public class ActiveServiceRequestCounterController {
    
    private ServiceRequestUpdateService serviceRequestUpdateService;

    @Autowired
    public ActiveServiceRequestCounterController(ServiceRequestUpdateService serviceRequestUpdateService) {
        this.serviceRequestUpdateService = serviceRequestUpdateService;
    }

    @Operation(summary = "REFRESH active service request counter", description = "Refreshes the active service request counter for the given managed object IDs.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request, Service request body is invalid!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseBody.class))),})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refresh(@Valid @RequestBody List<String> managedObjectIds) {
        // This is just a dummy endpoint to make the microservice settings available in the microservice UI.
        serviceRequestUpdateService.refreshServiceRequestCounterForManagedObjects(managedObjectIds);
        return ResponseEntity.status(200).build();
    }
}
