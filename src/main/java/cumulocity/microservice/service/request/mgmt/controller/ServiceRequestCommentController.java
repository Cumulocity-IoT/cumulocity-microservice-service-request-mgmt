package cumulocity.microservice.service.request.mgmt.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/service/request")
public class ServiceRequestCommentController {

	@Operation(summary = "Add new service request comment to specific service request.", description = "Each service request can have n comments. This endpoint adds a new comment to a specific service request.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestComment.class)))),
			@ApiResponse(responseCode = "404", description = "Not found")})
	@PostMapping(path = "/{serviceRequestId}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestComment>> createServiceRequestCommentList(
			@PathVariable Integer serviceRequestId, @RequestBody ServiceRequestComment serviceRequestComment) {
		List<ServiceRequestComment> dummy = new ArrayList<>();
		return new ResponseEntity<List<ServiceRequestComment>>(dummy, HttpStatus.OK);
	}

	@Operation(summary = "Returns all comments of specific service request by internal Id.", description = "Each service request can have n comments. This endpoint returns the complete list of comments of a specific service request.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestComment.class)))),
			@ApiResponse(responseCode = "404", description = "Not found")})
	@GetMapping(path = "/{serviceRequestId}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ServiceRequestComment>> getServiceRequestCommentList(@PathVariable Integer serviceRequestId) {
		List<ServiceRequestComment> dummy = new ArrayList<>();
		return new ResponseEntity<List<ServiceRequestComment>>(dummy, HttpStatus.OK);
	}
	
	@Operation(summary = "DELETE service request comment by Id", description = "", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "403", description = "Forbidden")})
	@DeleteMapping(path = "/{serviceRequestId}/comment/{commentId}")
	public ResponseEntity<Void> deleteServiceRequestCommentById(@PathVariable Integer serviceRequestId, @PathVariable String commentId) {
		String dummy = "dummy";
		if (dummy == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@Operation(summary = "PATCH service request comment by Id", description = "updates the service request comment by Id", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "403", description = "Forbidden")})
	@PatchMapping(path = "/{serviceRequestId}/comment/{commentId}")
	public ResponseEntity<Void> patchServiceRequestCommentById(@PathVariable Integer serviceRequestId, @PathVariable String commentId, @RequestBody ServiceRequestComment serviceRequestComment) {
		String dummy = "dummy";
		if (dummy == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
