package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRequestPriorityObjectMapper {
	public static final String MANAGEDOBJECGT_TYPE = "sr_PriorityList";
	
	public static final String SR_PRIORITY_LIST = "sr_PriorityList";
	public static final String SR_PRIORITY_NAME = "name";
	public static final String SR_PRIORITY_ORDINAL = "ordinal";
	
	private final ManagedObjectRepresentation managedObject;

	public ServiceRequestPriorityObjectMapper(ManagedObjectRepresentation manageObject) {
		super();
		this.managedObject = manageObject;
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestPriorityObjectMapper() {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestPriorityObjectMapper(String id) {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
		this.managedObject.setId(GId.asGId(id));
	}

	public ManagedObjectRepresentation getManageObject() {
		return managedObject;
	}
	
	public static ServiceRequestPriorityObjectMapper map2(List<ServiceRequestPriority> priorityList) {
		ServiceRequestPriorityObjectMapper mapper = new ServiceRequestPriorityObjectMapper();
		mapper.setPriorityList(priorityList);
		return mapper;
	}
	
	public static ServiceRequestPriorityObjectMapper map2(String id, List<ServiceRequestPriority> priorityList) {
		ServiceRequestPriorityObjectMapper mapper = new ServiceRequestPriorityObjectMapper(id);
		mapper.setPriorityList(priorityList);
		return mapper;
	}
	
	public static List<ServiceRequestPriority> map2(ManagedObjectRepresentation managedObject) {
		if(managedObject == null) {
			return new ArrayList<>();
		}
		ServiceRequestPriorityObjectMapper mapper = new ServiceRequestPriorityObjectMapper(managedObject);
		return mapper.getPriorityList();
	}
	
	public List<ServiceRequestPriority> getPriorityList() {
		List<ServiceRequestPriority> priorityList = new ArrayList<>();
		Object object = this.managedObject.get(SR_PRIORITY_LIST);
		List<HashMap<String, Object>> hashMapList = (List<HashMap<String, Object>>) object;
		for(HashMap<String, Object> hashMap: hashMapList) {
			ServiceRequestPriority priority = parsePriority(hashMap);
			if(priority != null) {
				priorityList.add(priority);
			}
		}

		/* Sort by ordinal */
		priorityList.sort((p1, p2) -> Long.compare(p1.getOrdinal(), p2.getOrdinal()));

		return priorityList;
	}
	
	public void setPriorityList(List<ServiceRequestPriority> priorityList) {
		this.managedObject.set(priorityList, SR_PRIORITY_LIST);
	}

	public String getId() {
		if(managedObject.getId() != null) {
			return managedObject.getId().getValue();			
		}
		return null;
	}
	
	private ServiceRequestPriority parsePriority(HashMap<String, Object> map) {
		if(map == null) {
			return null;
		}
		ServiceRequestPriority priority = new ServiceRequestPriority();
		priority.setOrdinal((Long)map.get(SR_PRIORITY_ORDINAL));
		priority.setName((String)map.get(SR_PRIORITY_NAME));
		return priority;
	}
	
	
	
	
}
