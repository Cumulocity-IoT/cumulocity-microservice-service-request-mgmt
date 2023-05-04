package cumulocity.microservice.service.request.mgmt.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestRef;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping("/api/adapter/service/request")
public class ServiceRequestExternalController {

	private ServiceRequestService serviceRequestService;
	
	
	@Autowired
	public ServiceRequestExternalController(ServiceRequestService serviceRequestService) {
		super();
		this.serviceRequestService = serviceRequestService;
	}

	@Operation(summary = "GET service request list", description = "Returns a list of all service requests in IoT Platform. Additional query parameter allow to filter that list. Parameter assigned=false returns all service requests which are not assigned to external object. Parameter assigned=true returns all assigned service requests.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequest>> getServiceRequestList(@Parameter(in = ParameterIn.QUERY, description = "filter, \"true\" returns all service request with external Id assigned, \"false\" returns service requests which doesn't have external Id assigned." , schema = @Schema()) @Valid @RequestParam(value = "assigned", required = true) Boolean assigned) {
		List<ServiceRequest> serviceRequestList = serviceRequestService.getCompleteActiveServiceRequestByFilter(assigned);
		return new ResponseEntity<List<ServiceRequest>>(serviceRequestList, HttpStatus.OK);
	}
	
    @Operation(summary = "SYNC service request into external object", description = "Triggers the adapter to update or create new external object at external system. If ServiceRequestRef contains already an externalId it is most likely an update. If the request body contains only internalId a new object must be created.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestRef.class))) })
	@PostMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestRef> syncServiceRequest(@PathVariable String serviceRequestId) {
		ServiceRequestRef serviceRequestRef = new ServiceRequestRef();
		return new ResponseEntity<ServiceRequestRef>(serviceRequestRef, HttpStatus.OK);
	}

	@Operation(summary = "UPDATE service request by external Id", description = "Updates specific service request.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@PutMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> updateServiceRequestByExternalId(
			@PathVariable String serviceRequestId, @RequestBody ServiceRequestPatchRqBody serviceRequestRqBody) {
		ServiceRequest dummy = new ServiceRequest();
		return new ResponseEntity<ServiceRequest>(dummy, HttpStatus.OK);
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


}
