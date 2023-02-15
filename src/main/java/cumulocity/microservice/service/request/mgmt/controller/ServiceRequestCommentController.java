package cumulocity.microservice.service.request.mgmt.controller;

import javax.validation.Valid;

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
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/service/request")
public class ServiceRequestCommentController {
	
	private ServiceRequestCommentService serviceRequestCommentService;
	private ServiceRequestService serviceRequestService;

	private ContextService<UserCredentials> contextService;
	
	@Autowired
	public ServiceRequestCommentController(ServiceRequestCommentService serviceRequestCommentService, ServiceRequestService serviceRequestService,
			ContextService<UserCredentials> contextService) {
		super();
		this.serviceRequestCommentService = serviceRequestCommentService;
		this.serviceRequestService = serviceRequestService;
		this.contextService = contextService;
	}

	@Operation(summary = "Add new service request comment to specific service request.", description = "Each service request can have n comments. This endpoint adds a new comment to a specific service request.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "404", description = "Not found")})
	@PostMapping(path = "/{serviceRequestId}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestComment> createServiceRequestComment(
			@PathVariable String serviceRequestId, @RequestBody ServiceRequestCommentRqBody serviceRequestComment) {
		ServiceRequest serviceRequest = serviceRequestService.getServiceRequestById(serviceRequestId);
		if(serviceRequest == null) {
			return new ResponseEntity<ServiceRequestComment>(HttpStatus.NOT_FOUND);
		}
		ServiceRequestComment newServiceRequestComment = serviceRequestCommentService.createComment(serviceRequest.getSource().getId(), serviceRequestId, serviceRequestComment, contextService.getContext().getUsername());
		return new ResponseEntity<ServiceRequestComment>(newServiceRequestComment, HttpStatus.OK);
	}

	@Operation(summary = "Returns all comments of specific service request by internal Id.", description = "Each service request can have n comments. This endpoint returns the complete list of comments of a specific service request.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceRequestComment.class)))),
			@ApiResponse(responseCode = "404", description = "Not found")})
	@GetMapping(path = "/{serviceRequestId}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RequestList<ServiceRequestComment>> getServiceRequestCommentList(@PathVariable String serviceRequestId,
			@Parameter(in = ParameterIn.QUERY, description = "Indicates how many entries of the collection shall be returned. The upper limit for one page is 2,000 objects.", schema = @Schema()) @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@Parameter(in = ParameterIn.QUERY, description = "The current page of the paginated results.", schema = @Schema()) @Valid @RequestParam(value = "currentPage", required = false) Integer currentPage,
			@Parameter(in = ParameterIn.QUERY, description = "When set to true, the returned result will contain in the statistics object the total number of pages. Only applicable on range queries." , schema = @Schema()) @Valid @RequestParam(value = "withTotalPages", required = false) Boolean withTotalPages) {
		RequestList<ServiceRequestComment> commentListByFilter = serviceRequestCommentService.getCommentListByFilter(serviceRequestId, pageSize, currentPage, withTotalPages);
		return new ResponseEntity<RequestList<ServiceRequestComment>>(commentListByFilter, HttpStatus.OK);
	}
	
	@Operation(summary = "DELETE service request comment by Id", description = "deletes specific service request comment. This operation is only allowed by owner of comment!", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "403", description = "Forbidden")})
	@DeleteMapping(path = "/comment/{commentId}")
	public ResponseEntity<Void> deleteServiceRequestCommentById(@PathVariable String commentId) {
		ServiceRequestComment comment = serviceRequestCommentService.getCommentById(commentId);
		if (comment == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		if(!comment.getOwner().equals(contextService.getContext().getUsername())) {
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}
		
		serviceRequestCommentService.deleteComment(commentId);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@Operation(summary = "PATCH service request comment by Id", description = "updates specific service request comment. This operation is only allowed by owner of comment!", tags = {})
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "403", description = "Forbidden")})
	@PatchMapping(path = "/comment/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceRequestComment> patchServiceRequestCommentById(@PathVariable String commentId, @RequestBody ServiceRequestComment serviceRequestComment) {
		ServiceRequestComment comment = serviceRequestCommentService.getCommentById(commentId);
		if (comment == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		if(!comment.getOwner().equals(contextService.getContext().getUsername())) {
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}
		
		ServiceRequestComment updatedComment = serviceRequestCommentService.updateComment(commentId, serviceRequestComment);
		return new ResponseEntity(updatedComment, HttpStatus.OK);
	}
}
