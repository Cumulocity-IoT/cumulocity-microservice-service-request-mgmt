package cumulocity.microservice.service.request.mgmt.service.c8y;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;

public class EventMapper {
	public static final String SR_EVENT_ID = "sr_EventId";

	private EventRepresentation event;
	
	public static EventMapper map2(String serviceRequestId, ServiceRequestDataRef eventRef) {
		if(eventRef == null || eventRef.getId() == null) {
			return null;
		}
		
		EventMapper mapper = new EventMapper(eventRef.getId());
		mapper.setServiceRequestEventId(serviceRequestId);
		return mapper;	
	}

	public EventMapper(String eventId) {
		super();
		this.event = new EventRepresentation();
		this.event.setId(GId.asGId(eventId));
	}
	
	public String getServiceRequestEventId() {
		return (String) event.get(SR_EVENT_ID);
	}
	
	public void setServiceRequestEventId(String serviceRequestEventId) {
		if(serviceRequestEventId == null) {
			return;
		}
		event.set(serviceRequestEventId, SR_EVENT_ID);
	}

	public EventRepresentation getEvent() {
		return event;
	}
	
}
