package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.Optional;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;

public interface ServiceRequestStatusConfigService {
	public List<ServiceRequestStatusConfig> createOrUpdateStatusList(List<ServiceRequestStatusConfig> serviceRequestStatusList);

	public List<ServiceRequestStatusConfig> getStatusList();

	public Optional<ServiceRequestStatusConfig> getStatus(String statusId);
	
	public void deleteStatus(String statusId);

	public boolean createDefaultStatusList();
}
