package cumulocity.microservice.service.request.mgmt.controller;

import java.util.ArrayList;
import java.util.List;

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

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/service/request/priority")
public class ServiceRequestPriorityController {

	@Operation(summary = "CREATE or UPDATE complete priority list", description = "Creates or updates complete priority list.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestPriority.class)))) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestPriority>> createServiceRequestPriorityList(
			@RequestBody List<ServiceRequestPriority> serviceRequestPriorityList) {
		List<ServiceRequestPriority> dummy = new ArrayList<>();
		return new ResponseEntity<List<ServiceRequestPriority>>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "GET service request priority list", description = "Returns complete list of priorities which are available.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestPriority.class)))) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestPriority>> getServiceRequestPriorityList() {
		List<ServiceRequestPriority> dummy = new ArrayList<>();
		return new ResponseEntity<List<ServiceRequestPriority>>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "GET service request priority by ordinal", description = "", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestPriority.class))),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{priorityOrdinal}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestPriority> getServiceRequestpriorityById(@PathVariable String priorityOrdinal) {
		ServiceRequestPriority dummy = new ServiceRequestPriority("high", 1L);
		if (dummy == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceRequestPriority>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "DELETE service request priority", description = "", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@DeleteMapping(path = "/{priorityOrdinal}")
	public ResponseEntity<Void> deleteServiceRequestpriorityById(@PathVariable String priorityOrdinal) {
		String dummy = "dummy";
		if (dummy == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
