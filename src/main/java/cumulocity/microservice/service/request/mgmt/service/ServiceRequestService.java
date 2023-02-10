package cumulocity.microservice.service.request.mgmt.service;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public interface ServiceRequestService {

	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequest, String owner);
	
	public ServiceRequest getServiceRequestById(Integer id);
	
	public ServiceRequest getServiceRequestByExternalId(String externalId);
	
	public RequestList<ServiceRequest> getServiceRequestByFilter(String deviceId, Integer pageSize);
	
	public void deleteServiceRequest(Integer id);
	
	
}
