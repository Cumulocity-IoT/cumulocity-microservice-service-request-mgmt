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
	public static final String SR_ACTIVE_ORDER_STATUS = "sr_ActiveOrderStatus";
	
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
		this.managedObjectRepresentation.set(managedObjectRepresentation.get(SR_ACTIVE_ORDER_STATUS), SR_ACTIVE_ORDER_STATUS);
	}

	public Map<String, Long> getServiceRequestPriorityCounterMap() {
		return (Map<String, Long>) managedObjectRepresentation.get(SR_ACTIVE_STATUS);
	}

	public Map<String, Long> getServiceRequestOrderPriorityCounterMap() {
		return (Map<String, Long>) managedObjectRepresentation.get(SR_ACTIVE_ORDER_STATUS);
	}

	public void updateServiceRequestPriorityCounter(RequestList<ServiceRequest> serviceRequestList, List<String> excludeList ) {
		if(serviceRequestList == null || serviceRequestList.getList() == null) {
			return;
		}
		
		Map<String, Long> priorityCounterMap = new HashMap<>();
		Map<String, Long> priorityOrderCounterMap = new HashMap<>();
		
		for (ServiceRequest serviceRequest : serviceRequestList.getList()) {
			if(serviceRequest.getStatus() != null && serviceRequest.getStatus().getId() != null && excludeList.contains(serviceRequest.getStatus().getId()) == false) {
				priorityCounterMap.merge(serviceRequest.getPriority().getName().replace(" ", "_"), 1L, Long::sum);
			}
			if(serviceRequest.getOrder() != null && serviceRequest.getOrder().getPriority() != null && serviceRequest.getOrder().getPriority().isEmpty() == false){
				priorityOrderCounterMap.merge(serviceRequest.getOrder().getPriority().replace(" ", "_"), 1L, Long::sum);
			}
		}
		
		for(String priority: priorityCounterMap.keySet()) {
			log.info("updateServiceRequestPriorityCounter(): Priority: {}, Count: {}", priority, priorityCounterMap.get(priority));
		}
		managedObjectRepresentation.set(priorityCounterMap, SR_ACTIVE_STATUS);
		managedObjectRepresentation.set(priorityOrderCounterMap, SR_ACTIVE_ORDER_STATUS);
	}


	public ManagedObjectRepresentation getManagedObjectRepresentation() {
		return managedObjectRepresentation;
	}
	
}
