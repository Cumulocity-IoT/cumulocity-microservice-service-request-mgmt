package cumulocity.microservice.service.request.mgmt.service.c8y;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public class AlarmMapper {
	public static final String SR_EVENT_ID = "sr_EventId";

	private AlarmRepresentation alarm;
	
	public static AlarmMapper map2(ServiceRequest serviceRequest) {
		if(serviceRequest == null || serviceRequest.getId() == null || serviceRequest.getAlarmRef() == null) {
			return null;
		}
		
		AlarmMapper mapper = new AlarmMapper(serviceRequest.getAlarmRef().getId());
		mapper.setServiceRequestEventId(serviceRequest.getId());
		return mapper;	
	}

	public AlarmMapper(String alarmId) {
		super();
		this.alarm = new AlarmRepresentation();
		this.alarm.setId(GId.asGId(alarmId));
		this.alarm.setStatus(CumulocityAlarmStatuses.ACKNOWLEDGED.toString());
	}
	
	public String getServiceRequestEventId() {
		return (String) alarm.get(SR_EVENT_ID);
	}
	
	public void setServiceRequestEventId(String serviceRequestEventId) {
		if(serviceRequestEventId == null) {
			return;
		}
		alarm.set(serviceRequestEventId, SR_EVENT_ID);
	}

	public AlarmRepresentation getAlarm() {
		return alarm;
	}
	
}
