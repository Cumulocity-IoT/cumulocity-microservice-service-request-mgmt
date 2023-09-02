package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusService;

@Service
public class ServiceRequestStatusServiceC8y implements ServiceRequestStatusService {

	private InventoryApi inventoryApi;
	
	public ServiceRequestStatusServiceC8y(InventoryApi inventoryApi) {
		this.inventoryApi = inventoryApi;
	}

	@Override
	public List<ServiceRequestStatus> createOrUpdateStatusList(List<ServiceRequestStatus> statusList) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if (managedObject == null) {
			/* CREATE */
			ManagedObjectRepresentation newManagedObject = inventoryApi
					.create(ServiceRequestStatusObjectMapper.map2(statusList).getManageObject());
			return ServiceRequestStatusObjectMapper.map2(newManagedObject);
		}
		/* UPDATE */
		ManagedObjectRepresentation updateManagedObject = ServiceRequestStatusObjectMapper
				.map2(managedObject.getId().getValue(), statusList).getManageObject();
		ManagedObjectRepresentation newManagedObject = inventoryApi.update(updateManagedObject);
		return ServiceRequestStatusObjectMapper.map2(newManagedObject);
	}

	@Override
	public List<ServiceRequestStatus> getStatusList() {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		return ServiceRequestStatusObjectMapper.map2(managedObject);
	}

	@Override
	public Optional<ServiceRequestStatus> getStatus(String statusId) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if(managedObject == null) {
			return Optional.empty();
		}
		List<ServiceRequestStatus> serviceRequestStatus = ServiceRequestStatusObjectMapper.map2(managedObject);

		Optional<ServiceRequestStatus> matchingObject = serviceRequestStatus.stream()
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

	private ManagedObjectRepresentation getManagedObjectRepresentation() {
		InventoryFilter filter = new InventoryFilter();
		filter.byType(ServiceRequestStatusObjectMapper.MANAGEDOBJECGT_TYPE);
		ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(filter);
		if (managedObjectsByFilter.get().iterator().hasNext()) {
			return managedObjectsByFilter.get().iterator().next();
		}
		return null;
	}
}
