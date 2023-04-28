package cumulocity.microservice.service.request.mgmt.service;

import org.springframework.core.io.Resource;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.service.c8y.EventAttachment;

public interface ServiceRequestCommentService {
	public ServiceRequestComment createComment(String deviceId, String serviceRequestId, ServiceRequestCommentRqBody serviceRequestComment, String owner);
	
	public RequestList<ServiceRequestComment> getCommentListByFilter(String serviceRequestId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public ServiceRequestComment getCommentById(String commentId);
	
	public void deleteComment(String commentId);
	
	public ServiceRequestComment updateComment(String commentId, ServiceRequestCommentRqBody serviceRequestComment);
	
	public void uploadAttachment(Resource resource, String contentType, byte[] fileBytes, String commentId);
	
	public EventAttachment downloadAttachment(String commentId);
}