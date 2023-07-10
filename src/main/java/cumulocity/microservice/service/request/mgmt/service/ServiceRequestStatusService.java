package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.Optional;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;

public interface ServiceRequestStatusService {
	public List<ServiceRequestStatus> createOrUpdateStatusList(List<ServiceRequestStatus> serviceRequestStatusList);

	public List<ServiceRequestStatus> getStatusList();

	public Optional<ServiceRequestStatus> getStatus(String statusId);
	
	public Optional<ServiceRequestStatus> getStatusByName(String statusName);

	public void deleteStatus(String statusId);
}
