package cumulocity.microservice.service.request.mgmt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestRef;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Service request controller
 * 
 * @author APES
 *
 */
@RestController
@RequestMapping("/api/service/request/external")
public class ServiceRequestExternalController {

    @Operation(summary = "SYNC service request into external object", description = "Triggers the adapter to update or create new external object at external system. If ServiceRequestRef contains already an externalId it is most likely an update. If the request body contains only internalId a new object must be created.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestRef.class))) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestRef> syncServiceRequest(@RequestBody ServiceRequestRef serviceRequestRef) {
		// TODO
		return new ResponseEntity<ServiceRequestRef>(serviceRequestRef, HttpStatus.OK);
	}

	@Operation(summary = "GET service request by external Id", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceRequestExternalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> getServiceRequestByExternalId(@PathVariable String serviceRequestExternalId) {
		ServiceRequest dummy = new ServiceRequest();
		return new ResponseEntity<ServiceRequest>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "UPDATE service request by external Id", description = "Updates specific service request.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@PutMapping(path = "/{serviceRequestExternalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> updateServiceRequestByExternalId(
			@PathVariable String serviceRequestExternalId, @RequestBody ServiceRequestPatchRqBody serviceRequestRqBody) {
		ServiceRequest dummy = new ServiceRequest();
		return new ResponseEntity<ServiceRequest>(dummy, HttpStatus.OK);
	}
}
