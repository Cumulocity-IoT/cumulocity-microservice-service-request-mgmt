package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusConfigService;

@Service
public class ServiceRequestStatusConfigServiceC8y implements ServiceRequestStatusConfigService {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceRequestStatusConfigServiceC8y.class);

	private InventoryApi inventoryApi;
	
	public ServiceRequestStatusConfigServiceC8y(@Qualifier("userInventoryApi") InventoryApi inventoryApi) {
		this.inventoryApi = inventoryApi;
	}

	@Override
	public List<ServiceRequestStatusConfig> createOrUpdateStatusList(List<ServiceRequestStatusConfig> statusList) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if (managedObject == null) {
			/* CREATE */
			ManagedObjectRepresentation newManagedObject = inventoryApi
					.create(ServiceRequestStatusConfigObjectMapper.map2(statusList).getManageObject());
			return ServiceRequestStatusConfigObjectMapper.map2(newManagedObject);
		}
		/* UPDATE */
		ManagedObjectRepresentation updateManagedObject = ServiceRequestStatusConfigObjectMapper
				.map2(managedObject.getId().getValue(), statusList).getManageObject();
		ManagedObjectRepresentation newManagedObject = inventoryApi.update(updateManagedObject);
		return ServiceRequestStatusConfigObjectMapper.map2(newManagedObject);
	}

	@Override
	public List<ServiceRequestStatusConfig> getStatusList() {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		return ServiceRequestStatusConfigObjectMapper.map2(managedObject);
	}

	@Override
	public Optional<ServiceRequestStatusConfig> getStatus(String statusId) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if(managedObject == null) {
			return Optional.empty();
		}
		List<ServiceRequestStatusConfig> serviceRequestStatus = ServiceRequestStatusConfigObjectMapper.map2(managedObject);

		Optional<ServiceRequestStatusConfig> matchingObject = serviceRequestStatus.stream()
				.filter(sr -> sr.getId().equals(statusId)).findFirst();
		return matchingObject;
	}
	
	
	@Override
	public void deleteStatus(String statusId) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if (managedObject == null) {
			return;
		}
		
		List<ServiceRequestStatus> statusList = ServiceRequestStatusObjectMapper.map2(managedObject);
		statusList.removeIf(sr -> sr.getId().equals(statusId));
		/* UPDATE */
		ManagedObjectRepresentation updateManagedObject = ServiceRequestStatusObjectMapper
				.map2(managedObject.getId().getValue(), statusList).getManageObject();
		inventoryApi.update(updateManagedObject);
	}

	protected ManagedObjectRepresentation getManagedObjectRepresentation() {
		InventoryFilter filter = new InventoryFilter();
		filter.byType(ServiceRequestStatusObjectMapper.MANAGEDOBJECGT_TYPE);
		ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(filter);
		if (managedObjectsByFilter.get().iterator().hasNext()) {
			return managedObjectsByFilter.get().iterator().next();
		}
		return null;
	}

	@Override
	public void createDefaultStatusList() {

		if(getManagedObjectRepresentation() != null) {
			LOG.info("Status list already defined, skipping creation of default status list!");
			return;
		}

		LOG.info("No status list defined, creating default status list!");

		List<ServiceRequestStatusConfig> defaultStatusList = new ArrayList<>();
		ServiceRequestStatusConfig status1 = new ServiceRequestStatusConfig();
		status1.setId("1");
		status1.setName("New");
		status1.setAlarmStatusTransition("ACKNOWLEDGED");
		status1.setIsClosedTransition(Boolean.FALSE);
		status1.setIsDeactivateTransition(Boolean.FALSE);
		status1.setIsExcludeForCounter(Boolean.FALSE);
		defaultStatusList.add(status1);

		ServiceRequestStatusConfig status2 = new ServiceRequestStatusConfig();
		status2.setId("2");
		status2.setName("Open");
		status2.setIsClosedTransition(Boolean.FALSE);
		status2.setIsDeactivateTransition(Boolean.FALSE);
		status2.setIsExcludeForCounter(Boolean.FALSE);
		defaultStatusList.add(status2);

		ServiceRequestStatusConfig status3 = new ServiceRequestStatusConfig();
		status3.setId("3");
		status3.setName("In Progress");
		status3.setIsClosedTransition(Boolean.FALSE);
		status3.setIsDeactivateTransition(Boolean.FALSE);
		status3.setIsExcludeForCounter(Boolean.FALSE);
		defaultStatusList.add(status3);
		
		ServiceRequestStatusConfig status4 = new ServiceRequestStatusConfig();
		status4.setId("4");
		status4.setName("Work done");
		status4.setIsClosedTransition(Boolean.FALSE);
		status4.setIsDeactivateTransition(Boolean.FALSE);
		status4.setIsExcludeForCounter(Boolean.TRUE);
		defaultStatusList.add(status4);

		ServiceRequestStatusConfig status5 = new ServiceRequestStatusConfig();
		status5.setId("5");
		status5.setName("Closed");
		status5.setAlarmStatusTransition("CLEARED");
		status5.setIsClosedTransition(Boolean.TRUE);
		status5.setIsDeactivateTransition(Boolean.FALSE);
		status5.setIsExcludeForCounter(Boolean.TRUE);
		defaultStatusList.add(status5);

		List<ServiceRequestStatusConfig> statusList = this.createOrUpdateStatusList(defaultStatusList);
		if(statusList == null || statusList.isEmpty()) {
			LOG.error("Failed to create default status list!");
			return;
		}

		return;
	}
}
