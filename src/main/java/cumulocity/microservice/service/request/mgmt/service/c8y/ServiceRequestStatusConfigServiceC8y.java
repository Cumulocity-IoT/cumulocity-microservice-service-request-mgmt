package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.List;
import java.util.Optional;

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

	private InventoryApi inventoryApi;
	
	public ServiceRequestStatusConfigServiceC8y(InventoryApi inventoryApi) {
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
}
