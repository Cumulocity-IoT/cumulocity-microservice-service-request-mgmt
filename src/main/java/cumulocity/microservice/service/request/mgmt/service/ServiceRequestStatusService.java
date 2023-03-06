package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;

public interface ServiceRequestStatusService {
	public List<ServiceRequestStatus> createOrUpdateStatusList(List<ServiceRequestStatus> serviceRequestStatusList);

	public List<ServiceRequestStatus> getStatusList();

	public ServiceRequestStatus getStatus(String statusId);

	public void deleteStatus(String statusId);
}
