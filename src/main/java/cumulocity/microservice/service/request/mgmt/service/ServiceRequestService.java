package cumulocity.microservice.service.request.mgmt.service;

import org.springframework.core.io.Resource;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public interface ServiceRequestService {
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequest, String owner);
	
	public ServiceRequest updateServiceRequest(String id, ServiceRequestPatchRqBody serviceRequest);
	
	public ServiceRequest getServiceRequestById(String id);
	
	public ServiceRequest getServiceRequestByExternalId(String externalId);
	
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String deviceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public RequestList<ServiceRequest> getActiveServiceRequestByFilter(String deviceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public void deleteServiceRequest(String id);

	public void uploadAttachment(Resource resource, String contentType, byte[] fileBytes, String serviceRequestId);
}
