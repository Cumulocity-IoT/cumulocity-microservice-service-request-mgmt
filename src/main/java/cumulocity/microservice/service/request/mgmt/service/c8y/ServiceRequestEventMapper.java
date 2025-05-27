package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestAttachment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;
import cumulocity.microservice.service.request.mgmt.model.ServiceOrder;

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
	public static final String SR_CLOSED = "sr_Closed";
	public static final String SR_EXTERNAL_ID = "sr_ExternalId";
	public static final String C8Y_IS_BINARY = "c8y_IsBinary";
	public static final String SR_SYNC_STATUS = "sr_SyncStatus";
	public static final String SR_ORDER = "sr_Order";
	public static final String SR_CUSTOM_PROPERTIES = "sr_CustomProperties";
	
	public enum SyncStatus {
		NEW, ACTIVE, STOP;
	}
	
	private final EventRepresentation event;
	
	public static ServiceRequestEventMapper map2(ServiceRequestPostRqBody serviceRequest) {
		if(serviceRequest == null) {
			return null;
		}
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper();
		mapper.setAlarmRef(new HashSet<>(Set.of(serviceRequest.getAlarmRef())));
		mapper.setDescription(serviceRequest.getDescription());
		mapper.setSource(serviceRequest.getSource());
		mapper.setEventRef(new HashSet<>(Set.of(serviceRequest.getEventRef())));
		mapper.setPriority(serviceRequest.getPriority());
		mapper.setSeriesRef(serviceRequest.getSeriesRef());
		mapper.setStatus(serviceRequest.getStatus());
		mapper.setTitle(serviceRequest.getTitle());
		mapper.setServiceRequestType(serviceRequest.getType());
		mapper.setOrder(serviceRequest.getOrder());
		mapper.setCustomProperties(serviceRequest.getCustomProperties());
		return mapper;
	}
	
	public static ServiceRequestEventMapper map2(String id, ServiceRequestPatchRqBody serviceRequest) {
		if(serviceRequest == null) {
			return null;
		}
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper(id);
		mapper.setDescription(serviceRequest.getDescription());
		mapper.setPriority(serviceRequest.getPriority());
		mapper.setStatus(serviceRequest.getStatus());
		mapper.setTitle(serviceRequest.getTitle());
		mapper.setIsActive(serviceRequest.getIsActive());
		mapper.setExternalId(serviceRequest.getExternalId());
		mapper.setOrder(serviceRequest.getOrder());
		mapper.setCustomProperties(serviceRequest.getCustomProperties());
		return mapper;
		
	}

	public static ServiceRequestEventMapper map2(String id, Set<ServiceRequestDataRef> alarmDataRefs) {
		if(alarmDataRefs == null) {
			return null;
		}

		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper(id);
		mapper.setAlarmRef(alarmDataRefs);
		return mapper;
	}
	
	public static ServiceRequest map2(EventRepresentation event) {
		if(event == null) {
			return null;
		}
		
		
		ServiceRequestEventMapper mapper = new ServiceRequestEventMapper(event);
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setAlarmRefList(mapper.getAlarmRefList());
		serviceRequest.setAlarmRef(mapper.getAlarmRefList().stream().findFirst().orElse(null));
		serviceRequest.setCreationTime(mapper.getCreationDateTime());
		serviceRequest.setDescription(mapper.getDescription());
		serviceRequest.setSource(mapper.getSource());
		serviceRequest.setEventRef(mapper.getEventRefList().stream().findFirst().orElse(null));
		serviceRequest.setEventRefList(mapper.getEventRefList());
		serviceRequest.setId(mapper.getId());
		serviceRequest.setLastUpdated(mapper.getLastUpdatedDateTime());
		serviceRequest.setOwner(mapper.getOwner());
		serviceRequest.setPriority(mapper.getPriority());
		serviceRequest.setSeriesRef(mapper.getSeriesRef());
		serviceRequest.setStatus(mapper.getStatus());
		serviceRequest.setTitle(mapper.getTitle());
		serviceRequest.setType(mapper.getServiceRequestType());
		serviceRequest.setIsActive(mapper.getIsActive());
		serviceRequest.setAttachment(mapper.getAttachment());
		serviceRequest.setExternalId(mapper.getExternalId());
		serviceRequest.setIsClosed(mapper.getIsClosed());
		serviceRequest.setOrder(mapper.getOrder());
		serviceRequest.setCustomProperties(mapper.getCustomProperties());
		return serviceRequest;
	}
	
	public ServiceRequestEventMapper() {
		event = new EventRepresentation();
		event.setDateTime(new DateTime());
		event.setType(EVENT_TYPE);
	}
	
	public ServiceRequestEventMapper(String id) {
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
		if(serviceRequestType == null) {
			return;
		}
		event.set(serviceRequestType.toString(), SR_TYPE);
	}
	
	public ServiceRequestStatus getStatus() {
		return parseStatus(event.get(SR_STATUS));
	}
	
	public void setStatus(ServiceRequestStatus status) {
		if(status == null) {
			return;
		}
		event.set(status, SR_STATUS);
	}
	
	public ServiceRequestDataRef getSeriesRef() {
		return  parseDataRef(event.get(SR_SERIES_REF));
	}
	
	public void setSeriesRef(ServiceRequestDataRef seriesRef) {
		if(seriesRef == null) {
			return;
		}
		event.set(seriesRef, SR_SERIES_REF);
	}
	
	public ServiceRequestPriority getPriority() {
		return parsePriority(event.get(SR_PRIORITY));
	}
	
	public void setPriority(ServiceRequestPriority priority) {
		if(priority == null) {
			return;
		}
		event.set(priority, SR_PRIORITY);
	}
	
	public String getOwner() {
		return (String) event.get(SR_OWNER);
	}
	
	public void setOwner(String owner) {
		if(owner == null) {
			return;
		}
		event.set(owner, SR_OWNER);
	}
	
	public Set<ServiceRequestDataRef> getEventRefList() {
		Object eventRefObj = event.get(SR_EVENT_REF);

		if(eventRefObj == null) {
			return null;
		}

		if(eventRefObj instanceof Collection) {
			Collection<Object> eventRefColl = (Collection<Object>) eventRefObj;
			Set<ServiceRequestDataRef> eventRefList = eventRefColl.stream().map(object -> parseDataRef(object)).collect(Collectors.toSet());
			return eventRefList;
		}

		return new HashSet(Set.of(parseDataRef(eventRefObj)));
	}
	
	public void setEventRef(Set<ServiceRequestDataRef> eventRefList) {
		if(eventRefList == null || eventRefList.isEmpty()) {
			return;
		}
		event.set(eventRefList, SR_EVENT_REF);
	}

	public Set<ServiceRequestDataRef> addEventRef(ServiceRequestDataRef eventRef) {
		Set<ServiceRequestDataRef> eventRefSet = getEventRefList();
		eventRefSet.add(eventRef);
		setEventRef(eventRefSet);
		return eventRefSet;
	}
	
	public ServiceRequestSource getSource() {
		return parseSource(event.getSource());
	}
	
	public void setSource(ServiceRequestSource source) {
		if(source == null) {
			return;
		}
		ManagedObjectRepresentation sourceMo = new ManagedObjectRepresentation();
		sourceMo.setId(GId.asGId(source.getId()));
		event.setSource(sourceMo);
	}
	
	public String getTitle() {
		return event.getText();	
	}
	
	public void setTitle(String title) {
		if(title == null) {
			return;
		}
		event.setText(title);
	}
	
	public String getDescription() {
		return (String) event.get(SR_DESCRIPTION);
	}
	
	public void setDescription(String description) {
		if(description == null) {
			return;
		}
		event.set(description, SR_DESCRIPTION);
	}
	
	public Set<ServiceRequestDataRef> getAlarmRefList() {
		Object alarmRefObj = event.get(SR_ALARM_REF);

		if(alarmRefObj == null) {
			return null;
		}

		if(alarmRefObj instanceof Collection) {
			Collection<Object> alarmRefColl = (Collection<Object>) alarmRefObj;
			Set<ServiceRequestDataRef> alarmRefList = alarmRefColl.stream().map(object -> parseDataRef(object)).collect(Collectors.toSet());
			return alarmRefList;
		}


		return new HashSet(Set.of(parseDataRef(alarmRefObj)));
	}
	
	public void setAlarmRef(Set<ServiceRequestDataRef> alarmRefList) {
		if(alarmRefList == null || alarmRefList.isEmpty()) {
			return;
		}
		event.set(alarmRefList, SR_ALARM_REF);
	}

	public Set<ServiceRequestDataRef> addAlarmRef(ServiceRequestDataRef alarmRef) {
		Set<ServiceRequestDataRef> alarmRefSet = getAlarmRefList();
		alarmRefSet.add(alarmRef);
		setAlarmRef(alarmRefSet);
		return alarmRefSet;
	}
	
	public void setExternalId(String externalId) {
		if(externalId == null) {
			return;
		}
		event.set(externalId, SR_EXTERNAL_ID);
	}
	
	public String getExternalId() {
		return (String)event.get(SR_EXTERNAL_ID);
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
	
	public void setId(String id) {
		if(id == null) {
			return;
		}
		
		event.setId(GId.asGId(id));
	}
	
	public Boolean getIsActive() {
		String active = (String) event.get(SR_ACTIVE);
		if(active == null) {
			return null;
		}
		return Boolean.valueOf(active);
	}
	
	public void setIsActive(Boolean isActive) {
		if(isActive == null) {
			return;
		}
		event.set(isActive.toString(), SR_ACTIVE);
	}
	
	public Boolean getIsClosed() {
		String isClosed = (String) event.get(SR_CLOSED);
		return Boolean.valueOf(isClosed);
	}
	
	public void setIsClosed(Boolean isClosed) {
		if(isClosed == null) {
			return;
		}
		event.set(isClosed.toString(), SR_CLOSED);
	}
	
	public SyncStatus getSyncStatus() {
		String syncStatus = (String) event.get(SR_SYNC_STATUS);
		if(syncStatus == null) {
			return SyncStatus.NEW;
		}
		return SyncStatus.valueOf(syncStatus);
	}
	
	public void setSyncStatus(SyncStatus syncStatus) {
		if(syncStatus == null) {
			return;
		}
		event.set(syncStatus.name(), SR_SYNC_STATUS);
	}
	
	public ServiceRequestAttachment getAttachment() {
		return parseAttachment(event.get(C8Y_IS_BINARY));
	}
	
	public void setAttachment(ServiceRequestAttachment attachment) {
		if(attachment == null) {
			return;
		}
		event.set(attachment, C8Y_IS_BINARY);
	}

	public void setCustomProperties(Map<String, String> customProperties) {
		if(customProperties == null) {
			return;
		}
		event.set(customProperties, SR_CUSTOM_PROPERTIES);
	}
	
	public Map<String, String> getCustomProperties() {
		Object obj = event.get(SR_CUSTOM_PROPERTIES);
		if (obj == null) {
			return null;
		}

		return (HashMap<String, String>) obj;
	}

	public void setOrder(ServiceOrder order) {
		if(order == null) {
			return;
		}
		event.set(order, SR_ORDER);
	}
	
	public ServiceOrder getOrder() {
		Object obj = event.get(SR_ORDER);
		return parseOrder(obj);
	}

	public EventRepresentation getEvent() {
		return event;
	}
	
	private ServiceRequestDataRef parseDataRef(Object obj) {
		if(obj == null) {
			return null;
		}
		if(obj instanceof ServiceRequestDataRef) {
			return (ServiceRequestDataRef) obj;
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
		
		ServiceRequestSource source = new ServiceRequestSource();
		source.setId(obj.getId().getValue());
		source.setSelf(obj.getSelf());
		source.setAdditionalProperty("name", obj.getName());
		return source;
	}
	
	private ServiceRequestAttachment parseAttachment(Object obj) {
		if(obj == null) {
			return null;
		}
		HashMap<String, Object> map = (HashMap<String, Object>) obj;

		ServiceRequestAttachment attachment = new ServiceRequestAttachment();
		attachment.setLength((Long)map.get("length"));
		attachment.setName((String)map.get("name"));
		attachment.setType((String)map.get("type"));
		return attachment;
	}

	private ServiceOrder parseOrder(Object obj) {
		if(obj == null) {
			return null;
		}
		if(obj instanceof ServiceOrder) {
			return (ServiceOrder) obj;
		}
	
		ObjectMapper mapper = new ObjectMapper();
		ServiceOrder order = mapper.convertValue(obj, ServiceOrder.class);
		return order;
	}
}
