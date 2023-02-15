package cumulocity.microservice.service.request.mgmt.service;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;

public interface ServiceRequestCommentService {
	public ServiceRequestComment createComment(String serviceRequestId, ServiceRequestComment serviceRequestComment, String owner);
	
	public RequestList<ServiceRequestComment> getCommentListByFilter(String serviceRequestId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public ServiceRequestComment getCommentById(String commentId);
	
	public void deleteComment(String commentId);
	
	public ServiceRequestComment updateComment(String commentId, ServiceRequestComment serviceRequestComment);
}