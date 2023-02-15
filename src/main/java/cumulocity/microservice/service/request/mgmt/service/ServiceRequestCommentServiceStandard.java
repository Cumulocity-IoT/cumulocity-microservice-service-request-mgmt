package cumulocity.microservice.service.request.mgmt.service;

import org.springframework.stereotype.Service;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;

@Service
public class ServiceRequestCommentServiceStandard implements ServiceRequestCommentService {

	@Override
	public ServiceRequestComment createComment(String serviceRequestId, ServiceRequestComment serviceRequestComment,
			String owner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestList<ServiceRequestComment> getCommentListByFilter(String serviceRequestId, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestComment getCommentById(String commentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteComment(String commentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceRequestComment updateComment(String commentId, ServiceRequestComment serviceRequestComment) {
		// TODO Auto-generated method stub
		return null;
	}

}
