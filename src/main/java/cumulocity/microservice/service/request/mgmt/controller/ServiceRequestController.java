package cumulocity.microservice.service.request.mgmt.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import cumulocity.microservice.service.request.mgmt.service.c8y.EventAttachment;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestServiceC8y.ServiceRequestValidationResult;
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
@Validated
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
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
		@ApiResponse(responseCode = "409", description = "Conflict, Alarm already assigned to service request!"),
		@ApiResponse(responseCode = "412", description = "Precondition Failed, Alarm of service request not found!"),})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> createServiceRequest(@Valid @RequestBody ServiceRequestPostRqBody serviceRequestRqBody) {
		log.info("CREATE service request: {}", serviceRequestRqBody);

		ServiceRequestValidationResult validateNewServiceRequest = serviceRequestService.validateNewServiceRequest(serviceRequestRqBody, contextService.getContext().getUsername());
		if(ServiceRequestValidationResult.MISSING_ALARM_REF.equals(validateNewServiceRequest)) {
			log.warn(validateNewServiceRequest.getMessage());
			return new ResponseEntity<ServiceRequest>(HttpStatus.BAD_REQUEST);
		}
		if(ServiceRequestValidationResult.ALARM_NOT_FOUND.equals(validateNewServiceRequest)) {
			log.warn(validateNewServiceRequest.getMessage());
			return new ResponseEntity<ServiceRequest>(HttpStatus.PRECONDITION_FAILED);
		}
		if(ServiceRequestValidationResult.ALARM_ASSIGNED.equals(validateNewServiceRequest)) {
			log.warn(validateNewServiceRequest.getMessage());
			return new ResponseEntity<ServiceRequest>(HttpStatus.CONFLICT);
		}
		ServiceRequest createServiceRequest = serviceRequestService.createServiceRequest(serviceRequestRqBody, contextService.getContext().getUsername());
		if(createServiceRequest == null) {
			log.warn("Service request creation failed! Data: {}", serviceRequestRqBody);
			return new ResponseEntity<ServiceRequest>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<ServiceRequest>(createServiceRequest, HttpStatus.CREATED);
	}

	@Operation(summary = "GET service request list", description = "Returns a list of all service requests in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all service requests which are not closed! With parameter all=true, all service requests will be returned without fillter.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RequestList<ServiceRequest>> getServiceRequestList(
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request equal source Id", schema = @Schema()) @Valid @RequestParam(value = "sourceId", required = false) String sourceId,
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request with status defined in that list", schema = @Schema()) @Valid @RequestParam(value = "statusList", required = false) String[] statusList,
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request with priority defined in that list", schema = @Schema()) @Valid @RequestParam(value = "priorityList", required = false) Long[] priorityList,
			@Parameter(in = ParameterIn.QUERY, description = "filter, \"true\" returns all service request, \"false\" (default) returns only active service requests." , schema = @Schema()) @Valid @RequestParam(value = "all", required = false) Boolean all,
			@Parameter(in = ParameterIn.QUERY, description = "Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects.", schema = @Schema()) @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@Parameter(in = ParameterIn.QUERY, description = "The current page of the paginated results.", schema = @Schema()) @Valid @RequestParam(value = "currentPage", required = false) Integer currentPage,
			@Parameter(in = ParameterIn.QUERY, description = "OrderBy, orders list by status and/or priority and/or timestamp.", schema = @Schema()) @Valid @RequestParam(value = "orderBy", required = false) String[] orderBy,
			@Parameter(in = ParameterIn.QUERY, description = "When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries." , schema = @Schema()) @Valid @RequestParam(value = "withTotalPages", required = false) Boolean withTotalPages) {
		
		RequestList<ServiceRequest> serviceRequestByFilter = new RequestList<>();
		if(all != null && all) {
			serviceRequestByFilter = serviceRequestService.getAllServiceRequestByFilter(sourceId, pageSize, currentPage, withTotalPages, statusList, priorityList, orderBy);
		}else {
			serviceRequestByFilter = serviceRequestService.getActiveServiceRequestByFilter(sourceId, pageSize, currentPage, withTotalPages, statusList, priorityList, orderBy);
		}

		return new ResponseEntity<RequestList<ServiceRequest>>(serviceRequestByFilter, HttpStatus.OK);
	}

	@Operation(summary = "GET service request by Id", description = "Returns service request by internal Id", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable String serviceRequestId) {
		ServiceRequest serviceRequest = serviceRequestService.getServiceRequestById(serviceRequestId);
		if(serviceRequest == null) {
			return new ResponseEntity<ServiceRequest>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceRequest>(serviceRequest, HttpStatus.OK);
	}

	@Operation(summary = "PUT service request by Id", description = "Updates specific service request.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@PutMapping(path = "/{serviceRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> updateServiceRequestById(@PathVariable String serviceRequestId,
			@Valid @RequestBody ServiceRequestPatchRqBody serviceRequestRqBody) {
		ServiceRequest serviceRequest = serviceRequestService.updateServiceRequest(serviceRequestId, serviceRequestRqBody);
		if(serviceRequest == null) {
			return new ResponseEntity<ServiceRequest>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ServiceRequest>(serviceRequest, HttpStatus.OK);
	}

	@Operation(summary = "DELETE service request by Id", description = "Delete a service request object at Cumulocity IoT. Related object at external system will not be deleted!", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@DeleteMapping(path = "/{serviceRequestId}")
	public void deleteServiceRequestById(@PathVariable String serviceRequestId) {
		serviceRequestService.deleteServiceRequest(serviceRequestId);
	}
	
	@Operation(summary = "UPLOAD attachment for specific service request", description = "Upload attachment from service request", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "409", description = "Conflict") })
	@PostMapping(path = "/{serviceRequestId}/attachment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> uploadServiceRequestAttachment(@PathVariable String serviceRequestId,
			@Parameter(in = ParameterIn.QUERY, description = "Mulitpart file, attachment", schema = @Schema()) @Valid @RequestParam("file") MultipartFile file,
			@Parameter(in = ParameterIn.QUERY, description = "Controls if the attachment can be overwritten. force == true means file will be overwritten if exists, otherwise a http 409 will be returned.", schema = @Schema()) @Valid @RequestParam("force") Boolean force) {
		
		try {
			byte[] fileBytes = file.getBytes();
			
			int responseCode = serviceRequestService.uploadAttachment(file.getResource(), file.getContentType(), fileBytes, serviceRequestId, force);
			return new ResponseEntity<Void>(HttpStatus.valueOf(responseCode));
		} catch (Exception e) {
			log.error("File uploaded failed!", e);
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "DOWNLOAD attachment for specific service request", description = "Download attachment from service request", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceRequestId}/attachment", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> downloadServiceRequestAttachment(@PathVariable String serviceRequestId) {
		try {
			EventAttachment attachment = serviceRequestService.downloadAttachment(serviceRequestId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(attachment.getContentType());
			headers.setContentDisposition(attachment.getContentDispostion());
			return new ResponseEntity<byte[]>(attachment.getAttachment(), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Add alarm reference to service request", description = "Add alarm reference to service request", tags = {})
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = "Ok"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
		@ApiResponse(responseCode = "409", description = "Conflict, Alarm already assigned to service request!"),
		@ApiResponse(responseCode = "412", description = "Precondition Failed, Alarm of service request not found!")})
	@PutMapping(path = "/{serviceRequestId}/alarm", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> addAlarmRefToServiceRequest(@PathVariable String serviceRequestId,
			@Valid @RequestBody ServiceRequestDataRef alarmRef) {
		ServiceRequestValidationResult validateAlarm = serviceRequestService.validateAlarm(alarmRef);
		if(ServiceRequestValidationResult.ALARM_NOT_FOUND.equals(validateAlarm)) {
			log.warn(validateAlarm.getMessage());
			return new ResponseEntity<ServiceRequest>(HttpStatus.PRECONDITION_FAILED);
		}
		if(ServiceRequestValidationResult.ALARM_ASSIGNED.equals(validateAlarm)) {
			log.warn(validateAlarm.getMessage());
			return new ResponseEntity<ServiceRequest>(HttpStatus.CONFLICT);
		}
		ServiceRequest serviceRequest = serviceRequestService.addAlarmRefToServiceRequest(serviceRequestId, alarmRef);
		if(serviceRequest == null) {
			return new ResponseEntity<ServiceRequest>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceRequest>(serviceRequest, HttpStatus.OK);
	}

}
