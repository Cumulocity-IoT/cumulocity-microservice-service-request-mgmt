package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.HashMap;
import java.util.Map;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import lombok.extern.slf4j.Slf4j;

public class ManagedObjectMapper {
	
	public static final String SR_ACTIVE_STATUS = "sr_ActiveStatus";
	
	private ManagedObjectRepresentation managedObjectRepresentation;
	
	public static ManagedObjectMapper map2(ManagedObjectRepresentation managedObjectRepresentation, ServiceRequest serviceRequest) {
		if(managedObjectRepresentation == null || serviceRequest == null|| serviceRequest.getPriority() == null) {
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
	
	public void addServiceRequestPriorityCounter(String priorityName) {
		if(priorityName == null) {
			return;
		}
		Object object = managedObjectRepresentation.get(SR_ACTIVE_STATUS);
		
		if(object == null) {
			Map<String, Long> priorityCounterMap = new HashMap();
			priorityCounterMap.put(priorityName, 1L);
			managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
			return;
		}
		
		Map<String, Long> priorityCounterMap = (Map<String, Long>) object;
		Long count = priorityCounterMap.get(priorityName);
		priorityCounterMap.put(priorityName, count != null ? count+1: 1);			
		managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
	}
	
	public void removeServiceRequestPriorityCounter(String priorityName) {
		if(priorityName == null) {
			return;
		}
		Object object = managedObjectRepresentation.get(SR_ACTIVE_STATUS);
		
		if(object == null) {
			Map<String, Long> priorityCounterMap = new HashMap();
			priorityCounterMap.put(priorityName, 0L);
			managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
			return;
		}
		
		Map<String, Long> priorityCounterMap = (Map<String, Long>) object;
		Long count = priorityCounterMap.get(priorityName);
		priorityCounterMap.put(priorityName, count-1);
		managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
	}



	public ManagedObjectRepresentation getManagedObjectRepresentation() {
		return managedObjectRepresentation;
	}
	
}
