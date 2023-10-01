package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRequestStatusConfigObjectMapper {
	public static final String MANAGEDOBJECGT_TYPE = "sr_StatusList";
	
	public static final String SR_STATUS_LIST = "sr_StatusList";
	public static final String SR_STATUS_NAME = "name";
	public static final String SR_STATUS_ID = "id";
	public static final String SR_ALARM_STATUS_TRANSITION = "alarmStatusTransition";
	public static final String SR_IS_CLOSED_TRANSITION = "isClosedTransition";
	public static final String SR_IS_DEACTIVATE_TRANSITON = "isDeactivateTransition";
	public static final String SR_IS_EXCLUDE_FOR_COUNTER = "isExcludeForCounter";
	
	private final ManagedObjectRepresentation managedObject;

	public ServiceRequestStatusConfigObjectMapper(ManagedObjectRepresentation manageObject) {
		super();
		this.managedObject = manageObject;
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestStatusConfigObjectMapper() {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestStatusConfigObjectMapper(String id) {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
		this.managedObject.setId(GId.asGId(id));
	}

	public ManagedObjectRepresentation getManageObject() {
		return managedObject;
	}
	
	public static ServiceRequestStatusConfigObjectMapper map2(List<ServiceRequestStatusConfig> statusList) {
		ServiceRequestStatusConfigObjectMapper mapper = new ServiceRequestStatusConfigObjectMapper();
		mapper.setStatusList(statusList);
		return mapper;
	}
	
	public static ServiceRequestStatusConfigObjectMapper map2(String id, List<ServiceRequestStatusConfig> statusList) {
		ServiceRequestStatusConfigObjectMapper mapper = new ServiceRequestStatusConfigObjectMapper(id);
		mapper.setStatusList(statusList);
		return mapper;
	}
	
	public static List<ServiceRequestStatusConfig> map2(ManagedObjectRepresentation managedObject) {
		if(managedObject == null) {
			return new ArrayList<>();
		}
		ServiceRequestStatusConfigObjectMapper mapper = new ServiceRequestStatusConfigObjectMapper(managedObject);
		return mapper.getStatusList();
	}
	
	public List<ServiceRequestStatusConfig> getStatusList() {
		List<ServiceRequestStatusConfig> statusList = new ArrayList<>();
		Object object = this.managedObject.get(SR_STATUS_LIST);
		List<HashMap<String, Object>> hashMapList = (List<HashMap<String, Object>>) object;
		for(HashMap<String, Object> hashMap: hashMapList) {
			ServiceRequestStatusConfig status = parseStatus(hashMap);
			if(status != null) {
				statusList.add(status);
			}
		}

		return statusList;
	}
	
	public void setStatusList(List<ServiceRequestStatusConfig> statusList) {
		this.managedObject.set(statusList, SR_STATUS_LIST);
	}

	public String getId() {
		if(managedObject.getId() != null) {
			return managedObject.getId().getValue();			
		}
		return null;
	}
	
	private ServiceRequestStatusConfig parseStatus(HashMap<String, Object> map) {
		if(map == null) {
			return null;
		}
		ServiceRequestStatusConfig status = new ServiceRequestStatusConfig();
		status.setId((String)map.get(SR_STATUS_ID));
		status.setName((String)map.get(SR_STATUS_NAME));
		status.setAlarmStatusTransition((String)map.get(SR_ALARM_STATUS_TRANSITION));
		status.setIsClosedTransition((Boolean)map.get(SR_IS_CLOSED_TRANSITION));
		status.setIsDeactivateTransition((Boolean)map.get(SR_IS_DEACTIVATE_TRANSITON));
		status.setIsExcludeForCounter((Boolean)map.get(SR_IS_EXCLUDE_FOR_COUNTER));
		return status;
	}
	
}
