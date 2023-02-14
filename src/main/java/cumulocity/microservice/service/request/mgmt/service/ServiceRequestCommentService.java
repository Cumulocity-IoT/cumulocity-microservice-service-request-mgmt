package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;

public interface ServiceRequestCommentService {
	public ServiceRequestComment createComment(ServiceRequestComment serviceRequestComment, String owner);
	
	public List<ServiceRequestComment> getCommentListByFilter(Long deviceId, Long serviceRequestId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
}