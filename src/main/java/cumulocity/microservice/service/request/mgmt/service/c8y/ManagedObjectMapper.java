package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public void updateServiceRequestPriorityCounter(RequestList<ServiceRequest> serviceRequestList, List<String> excludeList ) {
		if(serviceRequestList == null || serviceRequestList.getList() == null) {
			return;
		}
		
		Map<String, Long> priorityCounterMap = new HashMap<>();
		
		for (ServiceRequest serviceRequest : serviceRequestList.getList()) {
			if(excludeList.contains(serviceRequest.getStatus().getId()) == false) {
				priorityCounterMap.merge(serviceRequest.getPriority().getName().replace(" ", "_"), 1L, Long::sum);
			}
		}
		
		for(String priority: priorityCounterMap.keySet()) {
			log.info("updateServiceRequestPriorityCounter(): Priority: {}, Count: {}", priority, priorityCounterMap.get(priority));
		}
		managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
	}


	public ManagedObjectRepresentation getManagedObjectRepresentation() {
		return managedObjectRepresentation;
	}
	
}
