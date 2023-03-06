package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;

@Service
public class ServiceRequestPriorityServiceStandard implements ServiceRequestPriorityService {

	private InventoryApi inventoryApi;

	@Autowired
	public ServiceRequestPriorityServiceStandard(InventoryApi inventoryApi) {
		super();
		this.inventoryApi = inventoryApi;
	}

	@Override
	public List<ServiceRequestPriority> createOrUpdatePriorityList(List<ServiceRequestPriority> priorityList) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if (managedObject == null) {
			/* CREATE */
			ManagedObjectRepresentation newManagedObject = inventoryApi
					.create(ServiceRequestPriorityObjectMapper.map2(priorityList).getManageObject());
			return ServiceRequestPriorityObjectMapper.map2(newManagedObject);
		}
		/* UPDATE */
		ManagedObjectRepresentation updateManagedObject = ServiceRequestPriorityObjectMapper
				.map2(managedObject.getId().getValue(), priorityList).getManageObject();
		ManagedObjectRepresentation newManagedObject = inventoryApi.update(updateManagedObject);
		return ServiceRequestPriorityObjectMapper.map2(newManagedObject);
	}

	@Override
	public List<ServiceRequestPriority> getPriorityList() {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		return ServiceRequestPriorityObjectMapper.map2(managedObject);
	}

	@Override
	public ServiceRequestPriority getPriority(Long priorityOrdinal) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if(managedObject == null) {
			return null;
		}
		List<ServiceRequestPriority> serviceRequestPriority = ServiceRequestPriorityObjectMapper.map2(managedObject);

		Optional<ServiceRequestPriority> matchingObject = serviceRequestPriority.stream()
				.filter(sr -> sr.getOrdinal().equals(priorityOrdinal)).findFirst();
		return matchingObject.isEmpty() ? null: matchingObject.get();
	}

	@Override
	public void deletePriority(Long priorityOrdinal) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if (managedObject == null) {
			return;
		}
		
		List<ServiceRequestPriority> priorityList = ServiceRequestPriorityObjectMapper.map2(managedObject);
		priorityList.removeIf(sr -> sr.getOrdinal().equals(priorityOrdinal));
		/* UPDATE */
		ManagedObjectRepresentation updateManagedObject = ServiceRequestPriorityObjectMapper
				.map2(managedObject.getId().getValue(), priorityList).getManageObject();
		inventoryApi.update(updateManagedObject);
	}

	private ManagedObjectRepresentation getManagedObjectRepresentation() {
		InventoryFilter filter = new InventoryFilter();
		filter.byType(ServiceRequestPriorityObjectMapper.MANAGEDOBJECGT_TYPE);
		ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(filter);
		if (managedObjectsByFilter.get().iterator().hasNext()) {
			return managedObjectsByFilter.get().iterator().next();
		}
		return null;
	}

}
