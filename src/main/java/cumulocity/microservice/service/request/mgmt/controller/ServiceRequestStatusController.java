package cumulocity.microservice.service.request.mgmt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/service/request/status")
public class ServiceRequestStatusController {

	private ServiceRequestStatusService statusService;
	
	
	@Autowired
	public ServiceRequestStatusController(ServiceRequestStatusService statusService) {
		this.statusService = statusService;
	}

	@Operation(summary = "CREATE or UPDATE service request status list", description = "Creates or updates complete status list.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestStatus.class))) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestStatus>> createServiceRequestStatusList(
			@RequestBody List<ServiceRequestStatus> serviceRequestStatusList) {
		List<ServiceRequestStatus> newStatusList = statusService.createOrUpdateStatusList(serviceRequestStatusList);
		return new ResponseEntity<List<ServiceRequestStatus>>(newStatusList, HttpStatus.OK);
	}

	@Operation(summary = "GET service request status list", description = "Returns complete service request status list", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestStatus.class)))) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestStatus>> getServiceRequestStatusList() {
		List<ServiceRequestStatus> statusList = statusService.getStatusList();
		return new ResponseEntity<List<ServiceRequestStatus>>(statusList, HttpStatus.OK);
	}

	@Operation(summary = "GET service request status by Id", description = "Returns specific service request status by Id", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestStatus.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{statusId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestStatus> getServiceRequestStatusById(@PathVariable String statusId) {
		Optional<ServiceRequestStatus> status = statusService.getStatus(statusId);
		if (status.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceRequestStatus>(status.get(), HttpStatus.OK);
	}

	@Operation(summary = "DELETE service request status by Id", description = "", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@DeleteMapping(path = "/{statusId}")
	public ResponseEntity<Void> deleteServiceRequestStatusById(@PathVariable String statusId) {
		Optional<ServiceRequestStatus> status = statusService.getStatus(statusId);
		if (status.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		statusService.deleteStatus(statusId);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
