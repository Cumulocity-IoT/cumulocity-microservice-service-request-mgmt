package cumulocity.microservice.service.request.mgmt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRequestStatusObjectMapper {
	public static final String MANAGEDOBJECGT_TYPE = "sr_StatusList";
	
	public static final String SR_STATUS_LIST = "sr_StatusList";
	public static final String SR_STATUS_NAME = "name";
	public static final String SR_STATUS_ID = "id";
	
	private final ManagedObjectRepresentation managedObject;

	public ServiceRequestStatusObjectMapper(ManagedObjectRepresentation manageObject) {
		super();
		this.managedObject = manageObject;
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestStatusObjectMapper() {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
	}
	
	public ServiceRequestStatusObjectMapper(String id) {
		this.managedObject = new ManagedObjectRepresentation();
		this.managedObject.setType(MANAGEDOBJECGT_TYPE);
		this.managedObject.setId(GId.asGId(id));
	}

	public ManagedObjectRepresentation getManageObject() {
		return managedObject;
	}
	
	public static ServiceRequestStatusObjectMapper map2(List<ServiceRequestStatus> statusList) {
		ServiceRequestStatusObjectMapper mapper = new ServiceRequestStatusObjectMapper();
		mapper.setStatusList(statusList);
		return mapper;
	}
	
	public static ServiceRequestStatusObjectMapper map2(String id, List<ServiceRequestStatus> statusList) {
		ServiceRequestStatusObjectMapper mapper = new ServiceRequestStatusObjectMapper(id);
		mapper.setStatusList(statusList);
		return mapper;
	}
	
	public static List<ServiceRequestStatus> map2(ManagedObjectRepresentation managedObject) {
		if(managedObject == null) {
			return new ArrayList<>();
		}
		ServiceRequestStatusObjectMapper mapper = new ServiceRequestStatusObjectMapper(managedObject);
		return mapper.getStatusList();
	}
	
	public List<ServiceRequestStatus> getStatusList() {
		List<ServiceRequestStatus> statusList = new ArrayList<>();
		Object object = this.managedObject.get(SR_STATUS_LIST);
		List<HashMap<String, Object>> hashMapList = (List<HashMap<String, Object>>) object;
		for(HashMap<String, Object> hashMap: hashMapList) {
			ServiceRequestStatus status = parseStatus(hashMap);
			if(status != null) {
				statusList.add(status);
			}
		}

		return statusList;
	}
	
	public void setStatusList(List<ServiceRequestStatus> statusList) {
		this.managedObject.set(statusList, SR_STATUS_LIST);
	}

	public String getId() {
		if(managedObject.getId() != null) {
			return managedObject.getId().getValue();			
		}
		return null;
	}
	
	private ServiceRequestStatus parseStatus(HashMap<String, Object> map) {
		if(map == null) {
			return null;
		}
		ServiceRequestStatus status = new ServiceRequestStatus();
		status.setId((String)map.get(SR_STATUS_ID));
		status.setName((String)map.get(SR_STATUS_NAME));
		return status;
	}
	
}
