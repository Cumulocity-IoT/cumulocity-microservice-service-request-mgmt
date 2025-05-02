package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestPriorityService;

@Service
public class ServiceRequestPriorityServiceC8y implements ServiceRequestPriorityService {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceRequestPriorityServiceC8y.class);

	private InventoryApi inventoryApi;

	@Autowired
	public ServiceRequestPriorityServiceC8y(InventoryApi inventoryApi) {
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
	public ServiceRequestPriority getDefaultPriority() {
		List<ServiceRequestPriority> priorityList = getPriorityList();
		if (priorityList == null || priorityList.isEmpty()) {
			return new ServiceRequestPriority("default");
		}
		return priorityList.get(0);
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
	public ServiceRequestPriority getPriority(String priorityName) {
		ManagedObjectRepresentation managedObject = getManagedObjectRepresentation();
		if(managedObject == null) {
			return null;
		}
		List<ServiceRequestPriority> serviceRequestPriority = ServiceRequestPriorityObjectMapper.map2(managedObject);

		Optional<ServiceRequestPriority> matchingObject = serviceRequestPriority.stream()
				.filter(sr -> sr.getName().equals(priorityName)).findFirst();
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

	protected ManagedObjectRepresentation getManagedObjectRepresentation() {
		InventoryFilter filter = new InventoryFilter();
		filter.byType(ServiceRequestPriorityObjectMapper.MANAGEDOBJECGT_TYPE);
		ManagedObjectCollection managedObjectsByFilter = inventoryApi.getManagedObjectsByFilter(filter);
		if (managedObjectsByFilter.get().iterator().hasNext()) {
			return managedObjectsByFilter.get().iterator().next();
		}
		return null;
	}

	@Override
	public void createDefaultPriorityList() {

		if(getManagedObjectRepresentation() != null) {
			LOG.info("Priority list already defined, skipping creation of default priority list!");
			return;
		}

		LOG.info("No priority list defined, creating default priority list!");

		List<ServiceRequestPriority> defaultPriorityList = new ArrayList<>();
		ServiceRequestPriority priority1 = new ServiceRequestPriority();
		priority1.setOrdinal(1L);
		priority1.setName("high");
		defaultPriorityList.add(priority1);
		ServiceRequestPriority priority2 = new ServiceRequestPriority();
		priority2.setOrdinal(2L);
		priority2.setName("medium");
		defaultPriorityList.add(priority2);
		ServiceRequestPriority priority3 = new ServiceRequestPriority();
		priority3.setOrdinal(3L);
		priority3.setName("low");
		defaultPriorityList.add(priority3);
		
		List<ServiceRequestPriority> priorityList = this.createOrUpdatePriorityList(defaultPriorityList);
		if(priorityList == null || priorityList.isEmpty()) {
			LOG.error("Failed to create default priority list!");
			return;
		}

		return;
	}

}
