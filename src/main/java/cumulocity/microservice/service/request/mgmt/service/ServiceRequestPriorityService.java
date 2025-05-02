package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;

public interface ServiceRequestPriorityService {
	public List<ServiceRequestPriority> createOrUpdatePriorityList(List<ServiceRequestPriority> priorityList);

	public List<ServiceRequestPriority> getPriorityList();

	public ServiceRequestPriority getDefaultPriority();

	public ServiceRequestPriority getPriority(Long priorityOrdinal);

	public ServiceRequestPriority getPriority(String priorityName);

	public void deletePriority(Long priorityOrdinal);

	public void createDefaultPriorityList();
}
