package cumulocity.microservice.service.request.mgmt.service.c8y;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;

public class AlarmMapper {
	public static final String SR_EVENT_ID = "sr_EventId";

	private AlarmRepresentation alarm;
	
	public static AlarmMapper map2(String serviceRequestId, ServiceRequestDataRef alarmRef, CumulocityAlarmStatuses status) {
		if(alarmRef == null || alarmRef.getId() == null) {
			return null;
		}
		
		AlarmMapper mapper = new AlarmMapper(alarmRef.getId(), status);
		mapper.setServiceRequestEventId(serviceRequestId);
		return mapper;	
	}

	public AlarmMapper(String alarmId, CumulocityAlarmStatuses status) {
		super();
		this.alarm = new AlarmRepresentation();
		this.alarm.setId(GId.asGId(alarmId));
		this.alarm.setStatus(status.toString());
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
