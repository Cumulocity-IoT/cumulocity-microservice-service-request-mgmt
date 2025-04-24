package cumulocity.microservice.service.request.mgmt.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.service.c8y.EventFilterExtend;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestEventMapper;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestEventMapper.SyncStatus;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceRequestAlarmValidationService {


    private EventApi eventApi;

    private AlarmApi alarmApi;

    private final MicroserviceSubscriptionsService subscriptions;

    public ServiceRequestAlarmValidationService(EventApi eventApi, AlarmApi alarmApi, MicroserviceSubscriptionsService subscriptions) {
        this.eventApi = eventApi;
        this.alarmApi = alarmApi;
        this.subscriptions = subscriptions;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // 1 hour
    public void validateServiceRequestAlarmStatus() {
        log.info("Validating job is starting...");
        subscriptions.runForEachTenant(() -> {
            try {
                log.info("** STOP ** Validating stop service requests and check if alarm is cleared!");
                Map<String, AlarmRepresentation> invalidAlarmMap = getInvalidServiceRequestAlarmStatus(SyncStatus.STOP, CumulocityAlarmStatuses.CLEARED);
                log.info("** STOP **  Total number of invalid alarms: {}", invalidAlarmMap.size());
                for (Map.Entry<String, AlarmRepresentation> entry : invalidAlarmMap.entrySet()) {
                    String key = entry.getKey();
                    AlarmRepresentation alarm = entry.getValue();
                    log.info("** STOP **  Invalid service request Id / alarm ID: {}, Status: {}", key, alarm.getStatus());
                }

                log.info("** ACTIVE ** Validating active service requests and check if alarm is acknowledged!");
                Map<String, AlarmRepresentation> invalidAlarmMapActive = getInvalidServiceRequestAlarmStatus(SyncStatus.ACTIVE, CumulocityAlarmStatuses.ACKNOWLEDGED);
                log.info("** ACTIVE **  Total number of invalid alarms: {}", invalidAlarmMapActive.size());
                for (Map.Entry<String, AlarmRepresentation> entry : invalidAlarmMapActive.entrySet()) {
                    String key = entry.getKey();
                    AlarmRepresentation alarm = entry.getValue();
                    log.info("** ACTIVE **  Invalid service request Id / alarm ID: {}, Status: {}", key, alarm.getStatus());
                }
                log.info("** Validating job has finished!");
            } catch (Exception e) {
                log.error("Error validating service request alarm status", e);
            }
        });
    }

    private Map<String, AlarmRepresentation> getInvalidServiceRequestAlarmStatus(SyncStatus serviceRequestSyncStatus, CumulocityAlarmStatuses expectedAlarmStatus) {
        EventFilter eventFilter = new EventFilterExtend()
            .byType(ServiceRequestEventMapper.EVENT_TYPE)
            .byFromLastUpdateDate(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
            .byFragmentType(ServiceRequestEventMapper.SR_SYNC_STATUS)
            .byFragmentValue(serviceRequestSyncStatus.name()); 

        EventCollection eventCollection = eventApi.getEventsByFilter(eventFilter);

        Iterable<EventRepresentation> allPages = eventCollection.get(2000).allPages();
        Map<String, AlarmRepresentation> invalidAlarmMap = new HashMap<>();
        for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
            EventRepresentation eventRepresentation = iterator.next();
            ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
            sr.getAlarmRefList().forEach(serviceRequestDataRef -> {
                if (serviceRequestDataRef != null) {
                    try {
                        AlarmRepresentation alarm = alarmApi.getAlarm(GId.asGId(serviceRequestDataRef.getId()));
                        // Check if the alarm is not cleared and add it to the invalidAlarmMap if it is not
                        if(alarm != null && alarm.getStatus() != null && !alarm.getStatus().equals(expectedAlarmStatus.name())) {
                            invalidAlarmMap.put(sr.getId() + "/" + alarm.getId().getValue(), alarm);
                        }
                    } catch (Exception e) {
                        log.error("Alarm with ID {} not found", serviceRequestDataRef.getId(), e);
                    }
                }
            });
        }

        return invalidAlarmMap;
    }
}