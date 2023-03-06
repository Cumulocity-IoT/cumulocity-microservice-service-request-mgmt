package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;

@Service
public class ServiceRequestStatusServiceStandard implements ServiceRequestStatusService {

	@Override
	public List<ServiceRequestStatus> createOrUpdateStatusList(List<ServiceRequestStatus> serviceRequestStatusList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServiceRequestStatus> getStatusList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStatus getStatus(String statusId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteStatus(String statusId) {
		// TODO Auto-generated method stub

	}

}
