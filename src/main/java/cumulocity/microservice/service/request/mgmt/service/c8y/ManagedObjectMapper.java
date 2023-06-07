package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.HashMap;
import java.util.Map;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public class ManagedObjectMapper {
	
	public static final String SR_ACTIVE_STATUS = "sr_ActiveStatus";
	
	private ManagedObjectRepresentation managedObjectRepresentation;
	
	public static ManagedObjectMapper map2(ManagedObjectRepresentation managedObjectRepresentation) {
		if(managedObjectRepresentation == null) {
			return null;
		}
		
		ManagedObjectMapper mapper = new ManagedObjectMapper(managedObjectRepresentation);		
		return mapper;
	}
	
	public ManagedObjectMapper(ManagedObjectRepresentation managedObjectRepresentation) {
		this.managedObjectRepresentation = new ManagedObjectRepresentation();
		this.managedObjectRepresentation.setId(managedObjectRepresentation.getId());
		this.managedObjectRepresentation.set(managedObjectRepresentation.get(SR_ACTIVE_STATUS), SR_ACTIVE_STATUS);
	}

	public Map<String, Long> getServiceRequestPriorityCounterMap() {
		return (Map<String, Long>) managedObjectRepresentation.get(SR_ACTIVE_STATUS);
	}

	public void updateServiceRequestPriorityCounter(RequestList<ServiceRequest> serviceRequestList) {
		Map<String, Long> priorityCounterMap = new HashMap<>();

		for (ServiceRequest serviceRequest : serviceRequestList.getList()) {
			priorityCounterMap.merge(serviceRequest.getPriority().getName(), 1L, Long::sum);
		}
		
		managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
	}


	public ManagedObjectRepresentation getManagedObjectRepresentation() {
		return managedObjectRepresentation;
	}
	
}
