package cumulocity.microservice.service.request.mgmt.service;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public interface ServiceRequestService {
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequest, String owner);
	
	public ServiceRequest updateServiceRequest(Long id, ServiceRequestPatchRqBody serviceRequest);
	
	public ServiceRequest getServiceRequestById(Long id);
	
	public ServiceRequest getServiceRequestByExternalId(String externalId);
	
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String deviceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public RequestList<ServiceRequest> getActiveServiceRequestByFilter(String deviceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages);
	
	public void deleteServiceRequest(Long id);
}
