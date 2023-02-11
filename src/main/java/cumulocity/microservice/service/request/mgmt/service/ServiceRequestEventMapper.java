package cumulocity.microservice.service.request.mgmt.service;

import java.util.HashMap;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;

public class ServiceRequestEventMapper {
	public static final String EVENT_TYPE = "c8y_ServiceRequest";
	
	public static final String SR_TYPE = "sr_Type";
	public static final String SR_STATUS = "sr_Status";
	public static final String SR_SERIES_REF = "sr_SeriesRef";
	public static final String SR_PRIORITY = "sr_Priority";
	public static final String SR_OWNER = "sr_Owner";
	public static final String SR_EVENT_REF = "sr_EventRef";
	public static final String SR_DESCRIPTION = "sr_Description";
	public static final String SR_ALARM_REF = "sr_AlarmRef";
	public static final String SR_ACTIVE = "sr_Active";
	
	private final EventRepresentation event;
	
	public static ServiceRequestEventMapper map2(ServiceRequestPostRqBody serviceRequest) {
		if(serviceRequest == null) {
			return null;
		}
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper();
		mapper.setAlarmRef(serviceRequest.getAlarmRef());
		//event.set(serviceRequest.getCustomProperties(); TODO
		mapper.setDescription(serviceRequest.getDescription());
		mapper.setSource(serviceRequest.getSource());
		mapper.setEventRef(serviceRequest.getEventRef());
		mapper.setOwner(serviceRequest.getOwner());
		mapper.setPriority(serviceRequest.getPriority());
		mapper.setSeriesRef(serviceRequest.getSeriesRef());
		mapper.setStatus(serviceRequest.getStatus());
		mapper.setTitle(serviceRequest.getTitle());
		mapper.setServiceRequestType(serviceRequest.getType());
		return mapper;
	}
	
	public static ServiceRequestEventMapper map2(Long id, ServiceRequestPatchRqBody serviceRequest) {
		if(serviceRequest == null) {
			return null;
		}
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper(id);
		mapper.setDescription(serviceRequest.getDescription());
		mapper.setPriority(serviceRequest.getPriority());
		mapper.setStatus(serviceRequest.getStatus());
		mapper.setTitle(serviceRequest.getTitle());
		mapper.setIsActive(serviceRequest.getIsActive());
		return mapper;
		
	}
	
	public static ServiceRequest map2(EventRepresentation event) {
		if(event == null) {
			return null;
		}
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper(event);
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setAlarmRef(mapper.getAlarmRef());
		serviceRequest.setCreationTime(mapper.getCreationDateTime());
		//serviceRequest.setCustomProperties(event.get(EVENT_TYPE)); TODO
		serviceRequest.setDescription(mapper.getDescription());
		serviceRequest.setSource(mapper.getSource());
		serviceRequest.setEventRef(mapper.getEventRef());
		serviceRequest.setId(mapper.getId());
		serviceRequest.setLastUpdated(mapper.getLastUpdatedDateTime());
		serviceRequest.setOwner(mapper.getOwner());
		serviceRequest.setPriority(mapper.getPriority());
		serviceRequest.setSeriesRef(mapper.getSeriesRef());
		serviceRequest.setStatus(mapper.getStatus());
		serviceRequest.setTitle(mapper.getTitle());
		serviceRequest.setType(mapper.getServiceRequestType());
		serviceRequest.setIsActive(mapper.getIsActive());
		return serviceRequest;
	}
	
	public ServiceRequestEventMapper() {
		event = new EventRepresentation();
		event.setDateTime(new DateTime());
		event.setType(EVENT_TYPE);
	}
	
	public ServiceRequestEventMapper(Long id) {
		event = new EventRepresentation();
		event.setId(GId.asGId(id));
	}
	
	public ServiceRequestEventMapper(EventRepresentation event) {
		this.event = event;
		this.event.setType(EVENT_TYPE);
	}
	
	public ServiceRequestType getServiceRequestType() {
		return ServiceRequestType.fromValue((String) event.get(SR_TYPE));
	}
	
	public void setServiceRequestType(ServiceRequestType serviceRequestType) {
		event.set(serviceRequestType.toString(), SR_TYPE);
	}
	
	public ServiceRequestStatus getStatus() {
		return parseStatus(event.get(SR_STATUS));
	}
	
	public void setStatus(ServiceRequestStatus status) {
		event.set(status, SR_STATUS);
	}
	
	public ServiceRequestDataRef getSeriesRef() {
		return  parseDataRef(event.get(SR_SERIES_REF));
	}
	
	public void setSeriesRef(ServiceRequestDataRef seriesRef) {
		event.set(seriesRef, SR_SERIES_REF);
	}
	
	public ServiceRequestPriority getPriority() {
		return parsePriority(event.get(SR_PRIORITY));
	}
	
	public void setPriority(ServiceRequestPriority priority) {
		event.set(priority, SR_PRIORITY);
	}
	
	public String getOwner() {
		return (String) event.get(SR_OWNER);
	}
	
	public void setOwner(String owner) {
		event.set(owner, SR_OWNER);
	}
	
	public ServiceRequestDataRef getEventRef() {
		return parseDataRef(event.get(SR_EVENT_REF));
	}
	
	public void setEventRef(ServiceRequestDataRef eventRef) {
		event.set(eventRef, SR_EVENT_REF);
	}
	
	public ServiceRequestSource getSource() {
		return parseSource(event.getSource());
	}
	
	public void setSource(ServiceRequestSource source) {
		ManagedObjectRepresentation sourceMo = new ManagedObjectRepresentation();
		sourceMo.setId(GId.asGId(source.getId()));
		event.setSource(sourceMo);
	}
	
	public String getTitle() {
		return event.getText();	
	}
	
	public void setTitle(String title) {
		event.setText(title);
	}
	
	public String getDescription() {
		return (String) event.get(SR_DESCRIPTION);
	}
	
	public void setDescription(String description) {
		event.set(description, SR_DESCRIPTION);
	}
	
	public ServiceRequestDataRef getAlarmRef() {
		return parseDataRef(event.get(SR_ALARM_REF));
	}
	
	public void setAlarmRef(ServiceRequestDataRef alarmRef) {
		event.set(alarmRef, SR_ALARM_REF);
	}
	
	public void setExternalId(String externalId) {
		ExternalIDRepresentation externalIdRepresentation = new ExternalIDRepresentation();
		externalIdRepresentation.setExternalId(externalId);
		event.setExternalSource(externalIdRepresentation);
	}
	
	public DateTime getCreationDateTime() {
		return event.getCreationDateTime();
	}
	
	public DateTime getLastUpdatedDateTime() {
		return event.getLastUpdatedDateTime();
	}
	
	public String getId() {
		if(event.getId() != null) {
			return event.getId().getValue();			
		}
		return null;
	}	
	
	public void setId(Long id) {
		event.setId(GId.asGId(id));
	}
	
	public Boolean getIsActive() {
		Object obj = event.get(SR_ACTIVE);
		return obj != null;
	}
	
	public void setIsActive(Boolean isActive) {
		if(isActive) {
			event.set(new Object(), SR_ACTIVE);
		}else {
			event.set(null, SR_ACTIVE);
		}

	}
	
	public EventRepresentation getEvent() {
		return event;
	}
	
	private ServiceRequestDataRef parseDataRef(Object obj) {
		if(obj == null) {
			return null;
		}
		HashMap<String, String> map = (HashMap<String, String>) obj;
		ServiceRequestDataRef dataRef = new ServiceRequestDataRef();
		dataRef.setId(map.get("id"));
		dataRef.setUri(map.get("uri"));
		return dataRef;
	}
	
	private ServiceRequestStatus parseStatus(Object obj) {
		if(obj == null) {
			return null;
		}
		HashMap<String, String> map = (HashMap<String, String>) obj;
		ServiceRequestStatus status = new ServiceRequestStatus();
		status.setId(map.get("id"));
		status.setName(map.get("name"));
		return status;
	}
	
	private ServiceRequestPriority parsePriority(Object obj) {
		if(obj == null) {
			return null;
		}
		HashMap<String, Object> map = (HashMap<String, Object>) obj;
		ServiceRequestPriority priority = new ServiceRequestPriority();
		priority.setOrdinal((Long)map.get("ordinal"));
		priority.setName((String)map.get("name"));
		return priority;
	}
	
	private ServiceRequestSource parseSource(ManagedObjectRepresentation obj) {
		if(obj == null) {
			return null;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ServiceRequestSource source = new ServiceRequestSource();
		source.setId(obj.getId().getValue());
		source.setSelf(obj.getSelf());
		source.setAdditionalProperty("name", obj.getName());
		return source;
	}
}
