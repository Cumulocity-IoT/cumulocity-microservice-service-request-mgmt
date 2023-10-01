package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;

import org.springframework.core.io.Resource;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.service.c8y.EventAttachment;

public interface ServiceRequestCommentService {
	public ServiceRequestComment createComment(String deviceId, String serviceRequestId, ServiceRequestCommentRqBody serviceRequestComment, String username);
	
	public RequestList<ServiceRequestComment> getCommentListByFilter(String serviceRequestId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public List<ServiceRequestComment> getCompleteUserCommentListByServiceRequest(String serviceRequestId);	
	
	public List<ServiceRequestComment> getCompleteCommentListByServiceRequest(String serviceRequestId);	
	
	public ServiceRequestComment getCommentById(String commentId);
	
	public void deleteComment(String commentId);
	
	public ServiceRequestComment updateComment(String commentId, ServiceRequestCommentRqBody serviceRequestComment);
	
	public void uploadAttachment(Resource resource, String contentType, byte[] fileBytes, String commentId);
	
	public EventAttachment downloadAttachment(String commentId);
}