package cumulocity.microservice.service.request.mgmt.service.c8y;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlarmMapper {
	public static final String SR_EVENT_ID = "sr_EventId";

	private AlarmRepresentation alarm;
	
	public static AlarmMapper map2(String serviceRequestId, ServiceRequestDataRef alarmRef, CumulocityAlarmStatuses status) {
		if(alarmRef == null || alarmRef.getId() == null) {
			log.warn("Alarm reference is null! Cannot map Alarm for Service Request ID: {}", serviceRequestId);
			return null;
		}
		if(status == null) {
			log.warn("Alarm status is null for Alarm ID: {}. Alarm will not be updated.", alarmRef.getId());
			return null;
		}
		
		AlarmMapper mapper = new AlarmMapper(alarmRef.getId(), status);
		mapper.setServiceRequestEventId(serviceRequestId);
		return mapper;	
	}

	public AlarmMapper(String alarmId, CumulocityAlarmStatuses status) {
		this.alarm = new AlarmRepresentation();
		this.alarm.setId(GId.asGId(alarmId));
		if(status != null) {
			this.alarm.setStatus(status.toString());
		}
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
