package cumulocity.microservice.service.request.mgmt.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
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
@RequestMapping("/api/service/request")
public class ServiceRequestController {
	private static final Logger log = LoggerFactory.getLogger(ServiceRequestController.class);
	
	private ServiceRequestService serviceRequestService;

	private ContextService<UserCredentials> contextService;
	
	@Autowired
	public ServiceRequestController(ServiceRequestService serviceRequestService, ContextService<UserCredentials> contextService) {
		this.serviceRequestService = serviceRequestService;
		this.contextService = contextService;
	}

	@Operation(summary = "CREATE service request", description = "Creates a new service request object at Cumulocity IoT Platform.", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))) })
	@PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> createServiceRequest(@RequestBody ServiceRequestPostRqBody serviceRequestRqBody) {
		log.info("Service Request: {}", serviceRequestRqBody);
		ServiceRequest createServiceRequest = serviceRequestService.createServiceRequest(serviceRequestRqBody, contextService.getContext().getUsername());
		return new ResponseEntity<ServiceRequest>(createServiceRequest, HttpStatus.OK);
	}

	@Operation(summary = "GET service request list", description = "Returns a list of all service requests in IoT Platform. Additional query parameters allow to filter that list.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK") })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RequestList<ServiceRequest>> getServiceRequestList(
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request equal device Id", schema = @Schema()) @Valid @RequestParam(value = "deviceId", required = false) String deviceId,
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request equal status Id", schema = @Schema()) @Valid @RequestParam(value = "statusId", required = false) String statusId,
			@Parameter(in = ParameterIn.QUERY, description = "Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects.", schema = @Schema()) @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@Parameter(in = ParameterIn.QUERY, description = "When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries." , schema = @Schema()) @Valid @RequestParam(value = "withTotalPages", required = false) Boolean withTotalPages) {
		RequestList<ServiceRequest> serviceRequestByFilter = serviceRequestService.getServiceRequestByFilter(deviceId, pageSize);
		return new ResponseEntity<RequestList<ServiceRequest>>(serviceRequestByFilter, HttpStatus.OK);
	}

	@Operation(summary = "GET service request by Id", description = "Returns service request by internal Id", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable Integer serviceRequestId) {
		ServiceRequest serviceRequest = serviceRequestService.getServiceRequestById(serviceRequestId);
		if(serviceRequest == null) {
			return new ResponseEntity<ServiceRequest>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceRequest>(serviceRequest, HttpStatus.OK);
	}

	@Operation(summary = "PATCH service request by Id", description = "Updates specific service request.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@PatchMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> updateServiceRequestById(@PathVariable Integer serviceRequestId,
			@RequestBody ServiceRequestPatchRqBody serviceRequestRqBody) {
		ServiceRequest dummy = new ServiceRequest();
		return new ResponseEntity<ServiceRequest>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "DELETE service request by Id", description = "Delete a service request object at Cumulocity IoT. Related object at external system will not be deleted!", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@DeleteMapping(path = "/{serviceRequestId}")
	public void deleteServiceRequestById(@PathVariable Integer serviceRequestId) {
		serviceRequestService.deleteServiceRequest(serviceRequestId);
	}
}
