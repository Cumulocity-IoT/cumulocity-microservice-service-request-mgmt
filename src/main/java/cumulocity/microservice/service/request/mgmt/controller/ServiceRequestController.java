package cumulocity.microservice.service.request.mgmt.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequest.class))) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequest> createServiceRequest(@Valid @RequestBody ServiceRequestPostRqBody serviceRequestRqBody) {
		log.info("Service Request: {}", serviceRequestRqBody);
		ServiceRequest createServiceRequest = serviceRequestService.createServiceRequest(serviceRequestRqBody, contextService.getContext().getUsername());
		return new ResponseEntity<ServiceRequest>(createServiceRequest, HttpStatus.CREATED);
	}

	@Operation(summary = "GET service request list", description = "Returns a list of all service requests in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all service requests which are not closed! With parameter all=true, all service requests will be returned without fillter.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK") })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RequestList<ServiceRequest>> getServiceRequestList(
			@Parameter(in = ParameterIn.QUERY, description = "Filter, returns all service request equal source Id", schema = @Schema()) @Valid @RequestParam(value = "sourceId", required = false) String sourceId,
			@Parameter(in = ParameterIn.QUERY, description = "filter, \"true\" returns all service request, \"false\" (default) returns only active service requests." , schema = @Schema()) @Valid @RequestParam(value = "all", required = false) Boolean all,
			@Parameter(in = ParameterIn.QUERY, description = "Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects.", schema = @Schema()) @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@Parameter(in = ParameterIn.QUERY, description = "The current page of the paginated results.", schema = @Schema()) @Valid @RequestParam(value = "currentPage", required = false) Integer currentPage,
			@Parameter(in = ParameterIn.QUERY, description = "When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries." , schema = @Schema()) @Valid @RequestParam(value = "withTotalPages", required = false) Boolean withTotalPages) {
		
		RequestList<ServiceRequest> serviceRequestByFilter = new RequestList<>();
		if(all != null && all) {
			serviceRequestByFilter = serviceRequestService.getAllServiceRequestByFilter(sourceId, pageSize, currentPage, withTotalPages);			
		}else {
			serviceRequestByFilter = serviceRequestService.getActiveServiceRequestByFilter(sourceId, pageSize, currentPage, withTotalPages);
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
	public void uploadServiceRequestAttachment(@PathVariable String serviceRequestId,
			@Parameter(in = ParameterIn.QUERY, description = "Mulitpart file, attachment", schema = @Schema()) @Valid @RequestParam("file") MultipartFile file,
			@Parameter(in = ParameterIn.QUERY, description = "Controls if the attachment can be overwritten. force == true means file will be overwritten if exists, otherwise a http 409 will be returned.", schema = @Schema()) @Valid @RequestParam("force") Boolean force) {
		
		try {
			byte[] fileBytes = file.getBytes();
			
			serviceRequestService.uploadAttachment(file.getResource(), file.getContentType(), fileBytes, serviceRequestId);
		} catch (IOException e) {
			log.error("File uploaded failed!", e);
		}
	}
	
	@Operation(summary = "DOWNLOAD attachment for specific service request", description = "Download attachment from service request", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceRequestId}/attachment", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> downloadServiceRequestAttachment(@PathVariable String serviceRequestId) {
		Resource dummy;
		try {
			dummy = new UrlResource("http://dummy.com");
			return new ResponseEntity<Resource>(dummy, HttpStatus.OK);
		} catch (MalformedURLException e) {
			return new ResponseEntity<Resource>( HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
