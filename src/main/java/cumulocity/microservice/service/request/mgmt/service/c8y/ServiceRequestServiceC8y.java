package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PagingParam;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.event.PagedEventCollectionRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.google.common.base.Stopwatch;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusConfigService;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestEventMapper.SyncStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestServiceC8y implements ServiceRequestService {

    private final ServiceRequestPriorityServiceC8y serviceRequestPriorityServiceC8y;
	
	private EventApi eventApi;

	private AlarmApi alarmApi;

	private EventAttachmentApi eventAttachmentApi;

	private InventoryApi inventoryApi;
	
	private ServiceRequestStatusConfigService serviceRequestStatusConfigService;
	
	private ServiceRequestCommentService serviceRequestCommentService;

	private ServiceRequestUpdateService serviceRequestUpdateService;

	private ContextService<MicroserviceCredentials> contextService;

	private ContextService<UserCredentials> userContextService;

	private RawTextApi rawTextApi;

	
	public enum ServiceRequestValidationResult {
		ALARM_NOT_FOUND("Alarm doesn't exist!"),
		ALARM_ASSIGNED("Alarm already assigned to another service request!"),
		EVENT_NOT_FOUND("Event doesn't exist!"),
		EVENT_ASSIGNED("Event already assigned to another service request!"),
		VALID("Service request is valid"),
		MISSING_ALARM_REF("Alarm reference is missing"),
		MISSING_TYPE("Service Request type is missing or not supported"),
		MISSING_EVENT_REF("Event reference is missing"),
		MISSING_SOURCE("Source is missing, service request can't be created without source!"),
		MISSING_STATUS("Service Request status is missing, service request can't be created without status!"),
		MISSING_PRIORITY("Service Request priority is missing, service request can't be created without priority!"),
		ALARM_MUTUALLY_EXCLUSIVE("Alarm reference must be mutually exclusive. Set alarm or alarm reference, not both!"),
		EVENT_MUTUALLY_EXCLUSIVE("Event reference must be mutually exclusive. Set event or event reference, not both!");

		private String message;

		private ServiceRequestValidationResult(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

	@Autowired
	public ServiceRequestServiceC8y(EventApi eventApi, EventAttachmentApi eventAttachmentApi, AlarmApi alarmApi,
			InventoryApi inventoryApi, ServiceRequestStatusConfigService serviceRequestStatusConfigService, ServiceRequestCommentService serviceRequestCommentService, ServiceRequestUpdateService serviceRequestUpdateService, ContextService<MicroserviceCredentials> contextService, ContextService<UserCredentials> userContextService, ServiceRequestPriorityServiceC8y serviceRequestPriorityServiceC8y, RawTextApi rawTextApi) {
		this.eventApi = eventApi;
		this.eventAttachmentApi = eventAttachmentApi;
		this.alarmApi = alarmApi;
		this.inventoryApi = inventoryApi;
		this.serviceRequestStatusConfigService = serviceRequestStatusConfigService;
		this.serviceRequestCommentService = serviceRequestCommentService;
		this.serviceRequestUpdateService = serviceRequestUpdateService;
		this.contextService = contextService;
		this.userContextService = userContextService;
		this.serviceRequestPriorityServiceC8y = serviceRequestPriorityServiceC8y;
		this.rawTextApi = rawTextApi;
	}

	@Override
	public ServiceRequestValidationResult validateNewServiceRequest(ServiceRequestPostRqBody serviceRequestRqBody, String owner) {
		ServiceRequestType type = serviceRequestRqBody.getType();
		
		if (type == null) {
			return ServiceRequestValidationResult.MISSING_TYPE;
		}
		
		switch (type) {
			case ALARM:
				return validateAlarm(serviceRequestRqBody.getAlarmRef(), serviceRequestRqBody.getAlarm());
			case MAINTENANCE:
				return validateAlarm(serviceRequestRqBody.getAlarmRef(), serviceRequestRqBody.getAlarm());
			case DOWNTIME:
				// Decision to use no reference for downtime service requests!
				return ServiceRequestValidationResult.VALID;
			case NOTE:
				return validateEvent(serviceRequestRqBody.getEventRef(), serviceRequestRqBody.getEvent());
			case OTHER:
				return ServiceRequestValidationResult.VALID;
			default:
				return ServiceRequestValidationResult.MISSING_TYPE;
		}
	}

	/**
	 * Validates the alarm reference provided in the service request body.
	 * Checks if the alarm exists and whether it is already assigned to another service request.
	 *
	 * @param serviceRequestRqBody The service request body containing the alarm reference.
	 * @return A {@link ServiceRequestValidationResult} indicating the validation outcome:
	 *         - {@code MISSING_ALARM_REF} if the alarm reference is missing.
	 *         - {@code ALARM_NOT_FOUND} if the alarm does not exist.
	 *         - {@code ALARM_ASSIGNED} if the alarm is already assigned to another service request.
	 *         - {@code VALID} if the alarm is valid and not assigned.
	 */
	@Override
	public ServiceRequestValidationResult validateAlarm(ServiceRequestDataRef alarmRef, String alarmJsonString) {
		if(alarmRef == null && alarmJsonString == null) {
			return ServiceRequestValidationResult.MISSING_ALARM_REF;
		}

		if(alarmRef != null && alarmJsonString != null) {
			return ServiceRequestValidationResult.ALARM_MUTUALLY_EXCLUSIVE;
		}

		if(alarmJsonString != null) {
			return ServiceRequestValidationResult.VALID;
		}

		AlarmRepresentation alarm = null;

		try{
			alarm = alarmApi.getAlarm(GId.asGId(alarmRef.getId()));
		}catch(Exception e){
			log.error("Fetching alarm failed!", e);
		}

		
		if(alarm == null) {
			return ServiceRequestValidationResult.ALARM_NOT_FOUND;
		}
		// Check if the alarm already has a service request ID associated with it.
		// If `srId` is not null, it means the alarm is already assigned to another service request.
		Object srId = alarm.get(AlarmMapper.SR_EVENT_ID);
		if(srId != null) {
			// Return validation result indicating the alarm is already assigned.
			return ServiceRequestValidationResult.ALARM_ASSIGNED;
		}
		return ServiceRequestValidationResult.VALID;
	}

	@Override
	public ServiceRequestValidationResult validateEvent(ServiceRequestDataRef eventRef, String eventJsonString) {
		if (eventRef == null && eventJsonString == null) {
			return ServiceRequestValidationResult.MISSING_EVENT_REF;
		}

		if (eventRef != null && eventJsonString != null) {
			return ServiceRequestValidationResult.EVENT_MUTUALLY_EXCLUSIVE;
		}

		if(eventJsonString != null) {
			return ServiceRequestValidationResult.VALID;
		}
		
		EventRepresentation event = null;
		try{
			event = eventApi.getEvent(GId.asGId(eventRef.getId()));
		}catch(Exception e){
			log.error("Fetching event failed!", e);
		}
		
		if(event == null) {
			return ServiceRequestValidationResult.EVENT_NOT_FOUND;
		}
		Object srId = event.get(EventMapper.SR_EVENT_ID);
		if (srId != null) {
			return ServiceRequestValidationResult.EVENT_ASSIGNED;
			
		}
		return ServiceRequestValidationResult.VALID;
	}

	@Override
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequestRqBody, String owner) {
		log.info("createServiceRequest(serviceRequestRqBody {}, owner {})", serviceRequestRqBody.toString(), owner);

		if(serviceRequestRqBody.getAlarm() != null)	{
			AlarmRepresentation createdAlarm = rawTextApi.createAlarm(serviceRequestRqBody.getAlarm());
			if(createdAlarm == null) {
				log.debug("Creating alarm from json failed! Service Request can't be created!");
				return null;
			}
			serviceRequestRqBody.setAlarmRef(new ServiceRequestDataRef(createdAlarm.getId().getValue()));
			serviceRequestRqBody.setAlarm(null);
		}

		if(serviceRequestRqBody.getEvent() != null) {
			EventRepresentation createdEvent = rawTextApi.createEvent(serviceRequestRqBody.getEvent());
			if(createdEvent == null) {
				log.debug("Creating event from json failed! Service Request can't be created!");
				return null;
			}
			serviceRequestRqBody.setEventRef(new ServiceRequestDataRef(createdEvent.getId().getValue()));
			serviceRequestRqBody.setEvent(null);
		}

		List<ServiceRequestStatusConfig> statusList = serviceRequestStatusConfigService.getStatusList();
		List<String> excludeList = new ArrayList<>();

		if(serviceRequestRqBody.getStatus() == null) {
			serviceRequestRqBody.setStatus(
				statusList.stream()
					.filter(ServiceRequestStatusConfig::getIsInitialStatus)
					.findFirst()
					.map(srStatusConfig -> new ServiceRequestStatus(srStatusConfig.getId(), srStatusConfig.getName()))
					.orElse(null)
			);
		}

		ServiceRequestStatusConfig srStatus = null;

		for(ServiceRequestStatusConfig srStatusConfig: statusList) {
			if(Boolean.TRUE.equals(srStatusConfig.getIsExcludeForCounter())) {
				excludeList.add(srStatusConfig.getId());
			}
			if(srStatusConfig.getId().equals(serviceRequestRqBody.getStatus().getId())) {
				serviceRequestRqBody.getStatus().setName(srStatusConfig.getName());
				srStatus = srStatusConfig;
			}
		}
		
		if(srStatus == null) {
			log.error("Status {} is not part of the configured status list! Service Reqeust can't be updated!!!", serviceRequestRqBody.getStatus().toString());
			return null;
		}
		
		updateAlarmDataRef(serviceRequestRqBody.getAlarmRef());
		updateEventDataRef(serviceRequestRqBody.getEventRef());

		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(serviceRequestRqBody);
		eventMapper.setOwner(owner);
		eventMapper.setIsActive(Boolean.TRUE);
		eventMapper.setSyncStatus(SyncStatus.NEW);
		eventMapper.setPriority(findPriority(serviceRequestRqBody.getPriority()));
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());
		ServiceRequest newServiceRequest = ServiceRequestEventMapper.map2(createdEvent);

		//track status changes as system comment
		createCommentForStatusChange("Initial Status", newServiceRequest);
		
		// Alarm status transition
		if(newServiceRequest.getAlarmRefList() != null) {
			for(ServiceRequestDataRef alarmRef: newServiceRequest.getAlarmRefList()) {
				updateAlarm(newServiceRequest, alarmRef, srStatus);
			}
		}

		if(newServiceRequest.getEventRefList() != null) {
			for(ServiceRequestDataRef eventRef: newServiceRequest.getEventRefList()) {
				updateEvent(newServiceRequest.getId(), eventRef);
			}
		}
		
		// Update Managed Object
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(newServiceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);

		moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), excludeList);
		inventoryApi.update(moMapper.getManagedObjectRepresentation());

		return newServiceRequest;
	}

	private ServiceRequestPriority findPriority(ServiceRequestPriority priority) {
		if(priority == null) {
			return serviceRequestPriorityServiceC8y.getDefaultPriority();
		}
		
		if(priority.getOrdinal() != null) {
			return serviceRequestPriorityServiceC8y.getPriority(priority.getOrdinal());
		}else if(priority.getName() != null) {
			return serviceRequestPriorityServiceC8y.getPriority(priority.getName());
		}
		return serviceRequestPriorityServiceC8y.getDefaultPriority();
	}

	@Override
	public ServiceRequest updateServiceRequest(String id, ServiceRequestPatchRqBody serviceRequest) {
		log.info("updateServiceRequest(id {}, serviceRequestBody {})", id, serviceRequest.toString());
		Stopwatch stopwatch = Stopwatch.createStarted();
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(id, serviceRequest);
		if(serviceRequest.getPriority() != null) {
			eventMapper.setPriority(findPriority(serviceRequest.getPriority()));
		}

		if(serviceRequest.getExternalId() != null) {
			// if external ID is set, the sync status changes to active
			eventMapper.setSyncStatus(SyncStatus.ACTIVE);
		}
		ServiceRequest updatedServiceRequest = null;
		List<String> excludeList = new ArrayList<>();
		
		if(serviceRequest.getStatus() == null) {
			log.debug("Service Request update without status changes!");
			
			if(Boolean.FALSE.equals(eventMapper.getIsActive())) {
				log.info("Active status has changed to false, service request will be closed!");
				//TODO what should happen with alarm (maybe set back to ACTIVE)
				//TODO create a comment for seeting active to false?
				//TODO comments must be set to sr_Closed as well!
				eventMapper.setIsClosed(Boolean.TRUE);
				eventMapper.setSyncStatus(SyncStatus.STOP);
			}
			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
			updatedServiceRequest = ServiceRequestEventMapper.map2(updatedEvent);

			//if service request priority is changed, update the managed object, active counter
			if(serviceRequest.getPriority() != null) {
				List<ServiceRequestStatusConfig> statusList = serviceRequestStatusConfigService.getStatusList();
				for(ServiceRequestStatusConfig srStatusConfig: statusList) {
					if(Boolean.TRUE.equals(srStatusConfig.getIsExcludeForCounter())) {
						excludeList.add(srStatusConfig.getId());
					}
				}
				updateServiceRequestCounter(updatedServiceRequest, excludeList);
			}

		} else {	
			log.debug("Service Request update with status changes!");
			List<ServiceRequestStatusConfig> statusList = serviceRequestStatusConfigService.getStatusList();
			
			ServiceRequestStatusConfig srStatus = null;

			for(ServiceRequestStatusConfig srStatusConfig: statusList) {
				if(Boolean.TRUE.equals(srStatusConfig.getIsExcludeForCounter())) {
					excludeList.add(srStatusConfig.getId());
				}
				if(srStatusConfig.getId().equals(serviceRequest.getStatus().getId())) {
					srStatus = srStatusConfig;
				}
			}

			if(srStatus == null) {
				log.error("Status {} is not part of the configured status list! Service Reqeust can't be updated!!!", serviceRequest.getStatus().toString());
				return null;
			}
			
			//Name is optional, the name of the status config will be used
			serviceRequest.getStatus().setName(srStatus.getName());	
			eventMapper.setStatus(serviceRequest.getStatus());

			ServiceRequest originalServiceRequest = getServiceRequestById(id);

			if(originalServiceRequest == null) {
				log.error("Can't update Service Request with id {}", id);
				return null;
			}
			
			log.info("StatusConfig: {}", srStatus.toString());
			
			//Closing transition
			if(Boolean.TRUE.equals(srStatus.getIsClosedTransition())) {
				log.info("IsClosedTransition!");
				eventMapper.setIsClosed(Boolean.TRUE);
				eventMapper.setSyncStatus(SyncStatus.STOP);
			}
			
			//Deactivation transition
			if(Boolean.TRUE.equals(srStatus.getIsDeactivateTransition())) {
				log.info("IsDeactivateTransition!");
				eventMapper.setIsActive(Boolean.FALSE);
				eventMapper.setSyncStatus(SyncStatus.STOP);
			}

			if(Boolean.TRUE.equals(originalServiceRequest.getIsActive()) && Boolean.FALSE.equals(eventMapper.getIsActive())) {
				log.info("Active status has changed from true to false, service request will be closed!");
				eventMapper.setIsClosed(Boolean.TRUE);
				eventMapper.setSyncStatus(SyncStatus.STOP);
			}
			
			//With isSynchronisationActive the sync status can be set to active and overwrites the default behaviour
			if(Boolean.TRUE.equals(srStatus.getIsSynchronisationActive())) {
				eventMapper.setSyncStatus(SyncStatus.ACTIVE);
			}

			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());

			updatedServiceRequest = ServiceRequestEventMapper.map2(updatedEvent);

			if(!originalServiceRequest.getStatus().getId().equals(updatedServiceRequest.getStatus().getId())) {
				//track status changes as system comment
				createCommentForStatusChange("Updated Status", updatedServiceRequest);

				// Alarm status transition, update all alarms
				if(updatedServiceRequest.getAlarmRefList() != null) {
					for (ServiceRequestDataRef alarmRef : updatedServiceRequest.getAlarmRefList()) {
						updateAlarm(updatedServiceRequest, alarmRef, srStatus);
					}
				}
			}

			//if service request is closed all comments must also be set to closed
			if(updatedServiceRequest.getIsClosed()) {
				updateAllCommentsToClosed(updatedServiceRequest);
			}

		}
		
		updateServiceRequestCounter(updatedServiceRequest, excludeList);

		stopwatch.stop();
		long ms = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		log.info("updateServiceRequest(id: {}): return in {} ms", id, ms);
		return updatedServiceRequest;
	}

	@Override
	public ServiceRequest getServiceRequestById(String id) {
		try {
			EventRepresentation event = eventApi.getEvent(GId.asGId(id));
			return ServiceRequestEventMapper.map2(event);
		}catch(SDKException e) {
			log.error("Fetching service request with id: " + id + " failed!");
		}
		return null;
	}

	@Override
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String sourceId, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages, String[] statusList, Long[] priorityList, String[] orderBy, ServiceRequestType type, Boolean withSourceAssets, Boolean withSourceDevices) {
		log.info("getAllServiceRequestByFilter(sourceId: {}, pageSize: {}, pageNumber: {}, withTotalPages: {}, statusList: {}, priorityList: {}, orderBy: {}, withSourceAssets: {}, withSourceDevices: {})", sourceId, pageSize, pageNumber, withTotalPages, statusList, priorityList, orderBy, withSourceAssets, withSourceDevices);
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		if(type != null) {
			filter.byFragmentType(ServiceRequestEventMapper.SR_TYPE);
			filter.byFragmentValue(type.toString());
		}
		if (sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(withSourceAssets).setWithSourceDevices(withSourceDevices);
		}
		boolean isStatusFilter = ArrayUtils.isNotEmpty(statusList);
		boolean isPriorityFilter = ArrayUtils.isNotEmpty(priorityList);
		boolean isOrderBy = ArrayUtils.isNotEmpty(orderBy);
		
		if(isStatusFilter || isPriorityFilter || isOrderBy) {
			Predicate<ServiceRequest> filterPredicate = sr -> sr.getStatus() != null && sr.getPriority() != null;
			
			if(isStatusFilter) {
				filterPredicate.and(sr -> ArrayUtils.contains(statusList, sr.getStatus().getId()));
			}
			
			if(isPriorityFilter) {
				filterPredicate.and(sr -> ArrayUtils.contains(priorityList, sr.getPriority().getOrdinal()));
			}
						
			return getServiceRequestByFilterAndInternalFilter(filter, filterPredicate, pageSize, pageNumber, withTotalPages, orderBy);
		}
		
		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public RequestList<ServiceRequest> getActiveServiceRequestByFilter(String sourceId, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages, String[] statusList, Long[] priorityList, String[] orderBy, ServiceRequestType type, Boolean withSourceAssets, Boolean withSourceDevices) {
		log.info("getActiveServiceRequestByFilter(sourceId: {}, pageSize: {}, pageNumber: {}, withTotalPages: {}, statusList: {}, priorityList: {}, orderBy: {}, withSourceAssets: {}, withSourceDevices: {})", sourceId, pageSize, pageNumber, withTotalPages, statusList, priorityList, orderBy, withSourceAssets, withSourceDevices);
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE);
		filter.byFragmentValue(Boolean.TRUE.toString());
		if (sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(withSourceAssets).setWithSourceDevices(withSourceDevices);
		}
		
		boolean isStatusFilter = ArrayUtils.isNotEmpty(statusList);
		boolean isPriorityFilter = ArrayUtils.isNotEmpty(priorityList);
		boolean isTypeFilter = type != null;
		if(isStatusFilter || isPriorityFilter || isTypeFilter) {
			Predicate<ServiceRequest> filterPredicate = sr -> sr.getStatus() != null && sr.getPriority() != null;
			
			if(isStatusFilter) {
				filterPredicate = filterPredicate.and(sr -> ArrayUtils.contains(statusList, sr.getStatus().getId()));
			}
			
			if(isPriorityFilter) {
				filterPredicate = filterPredicate.and(sr -> ArrayUtils.contains(priorityList, sr.getPriority().getOrdinal()));
			}

			if(isTypeFilter) {
				filterPredicate = filterPredicate.and(sr -> sr.getType() != null && sr.getType().equals(type));
			}
						
			return getServiceRequestByFilterAndInternalFilter(filter, filterPredicate, pageSize, pageNumber, withTotalPages, orderBy);
		}
		
		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public Collection<ServiceRequest> getAllServiceRequestBySyncStatus(Boolean assigned) {
		log.info("getCompleteActiveServiceRequestByFilter(assigned: {})", assigned);
		Stopwatch stopwatch = Stopwatch.createStarted();
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_SYNC_STATUS);
		if(assigned != null && assigned) {
			// service request which must be updated
			filter.byFragmentValue(String.valueOf(SyncStatus.ACTIVE.name()));
		}else if(assigned != null && !assigned) {
			// service request which are new
			filter.byFragmentValue(String.valueOf(SyncStatus.NEW.name()));
		}

		log.debug("Filter; Type: {}, FragmentType: {}, FragmentValue: {}", filter.getType(), filter.getFragmentType(), filter.getFragmentValue());
		EventCollection eventList = eventApi.getEventsByFilter(filter);
		Iterable<EventRepresentation> allPages = eventList.get(2000).allPages();
		Set<ServiceRequest> serviceRequestList = new HashSet<ServiceRequest>();
		for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
			EventRepresentation eventRepresentation = iterator.next();
			if(assigned == null) {
				ServiceRequestEventMapper eventMapper = new ServiceRequestEventMapper(eventRepresentation);
				SyncStatus syncStatus = eventMapper.getSyncStatus();
				if(syncStatus.equals(SyncStatus.NEW) || syncStatus.equals(SyncStatus.ACTIVE)) {
					ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
					if(sr.getStatus() == null) {
						log.error("Service Request has no status, seems something wrong with the event! Event:");
						log.error(eventRepresentation.toJSON());
					}else {
						serviceRequestList.add(sr);
					}
				}
			}else {
				ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);	
				if(sr.getStatus() == null) {
					log.error("Service Request has no status, seems something wrong with the event! Event:");
					log.error(eventRepresentation.toJSON());
				}else{
					serviceRequestList.add(sr);
				}
			}
		}
		stopwatch.stop();
		long ms = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		log.info("getCompleteActiveServiceRequestByFilter(assigned: {}): return list.size {} in {} ms", assigned, serviceRequestList.size(), ms);
		return serviceRequestList;
	}

	@Override
	public Collection<ServiceRequest> getAllServiceRequestBySyncStatus(Boolean assigned, String[] serviceRequestIds) {
		Set<ServiceRequest> serviceRequestList = new HashSet<ServiceRequest>();

		if(serviceRequestIds == null || serviceRequestIds.length <= 0) {
			log.warn("No service request IDs defined!");
			return serviceRequestList;
		}

		log.info("getAllServiceRequestBySyncStatus(assigned: {}, serviceRequestIds: {})", assigned, serviceRequestIds);
		Stopwatch stopwatch = Stopwatch.createStarted();

		List<EventRepresentation> events = new ArrayList<>();
		for(String id: serviceRequestIds) {
			try {
				EventRepresentation event = eventApi.getEvent(GId.asGId(id));
				events.add(event);
			}catch(Exception e) {
				log.error("Fetching event with id {} failed!", id);
			}
		}

		log.debug("Events found for IDs: count= {}", events.size());


		if(events.size() > 0 ) {
			Predicate<EventRepresentation> filterPredicate;

			if(assigned != null && assigned) {
				filterPredicate = event -> event.getType().equals(ServiceRequestEventMapper.EVENT_TYPE) && event.get(ServiceRequestEventMapper.SR_SYNC_STATUS).equals(String.valueOf(SyncStatus.ACTIVE.name()));
			}else if(assigned != null && !assigned) {
				filterPredicate = event -> event.getType().equals(ServiceRequestEventMapper.EVENT_TYPE) && event.get(ServiceRequestEventMapper.SR_SYNC_STATUS).equals(String.valueOf(SyncStatus.NEW.name()));
			}else {
				filterPredicate = event -> event.getType().equals(ServiceRequestEventMapper.EVENT_TYPE);
			}
			serviceRequestList= events.stream().filter(filterPredicate).map(event -> {
				return ServiceRequestEventMapper.map2(event);
			}).collect(Collectors.toSet());
		}

		stopwatch.stop();
		long ms = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		log.info("getAllServiceRequestBySyncStatus(assigned: {}, serviceRequestIds: {}): return list.size {} in {} ms", assigned, serviceRequestIds, serviceRequestList.size(), ms);
		return serviceRequestList;
	}

	@Override
	public void deleteServiceRequest(String id) {
		EventRepresentation eventRepresentation = new EventRepresentation();
		eventRepresentation.setId(GId.asGId(id));
		eventApi.delete(eventRepresentation);
	}

	private RequestList<ServiceRequest> getServiceRequestByFilter(EventFilter filter, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);

		// TODO return specific page, eventList.getPage(null, 0, 0)
		PagedEventCollectionRepresentation pagedCollection = getPagedEventCollection(eventList, pageSize,
				withTotalPages);
		if (pageNumber != null) {
			pagedCollection = eventList.getPage(pagedCollection, pageNumber, pageSize);
		}

		PageStatisticsRepresentation pageStatistics = pagedCollection.getPageStatistics();

		List<EventRepresentation> events = pagedCollection.getEvents();

		List<ServiceRequest> serviceRequestList = events.stream().map(event -> {
			return ServiceRequestEventMapper.map2(event);
		}).collect(Collectors.toList());

		RequestList<ServiceRequest> requestList = new RequestList<>();
		requestList.setCurrentPage(pageStatistics.getCurrentPage());
		requestList.setList(serviceRequestList);
		requestList.setPageSize(pageStatistics.getPageSize());
		requestList.setTotalPages(pageStatistics.getTotalPages());
		requestList.setTotalElements(pageStatistics.getTotalElements());
		return requestList;
	}

	protected RequestList<ServiceRequest> getAllActiveEventsBySource(GId sourceId) {
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE).bySource(sourceId).byFragmentType(ServiceRequestEventMapper.SR_ACTIVE).byFragmentValue(Boolean.TRUE.toString());
		//TODO if the number of events is more than 2000, we need to implement pagination!!!
		return getServiceRequestByFilter(filter, 2000, null, null);
	}

	private RequestList<ServiceRequest> getServiceRequestByFilterAndInternalFilter(EventFilter filter,
			Predicate<ServiceRequest> serviceRequestFilter, Integer pageSize, Integer pageNumber,
			Boolean withTotalPages, final String[] orderBy) {
		
		Stopwatch totalStopwatch = Stopwatch.createStarted();
		
		pageNumber = pageNumber != null ? pageNumber : 1;
		pageSize = pageSize != null ? pageSize : 5;
		
		// Measure fetching from Cumulocity
		Stopwatch fetchStopwatch = Stopwatch.createStarted();
		EventCollection eventList = eventApi.getEventsByFilter(filter);
		Iterable<EventRepresentation> allPages = eventList.get(10).allPages();
		fetchStopwatch.stop();
		log.info("Fetching events from C8y: {} ms", fetchStopwatch.elapsed(TimeUnit.MILLISECONDS));
		
		// Measure mapping and filtering
		Stopwatch mappingStopwatch = Stopwatch.createStarted();
		List<ServiceRequest> serviceRequestList = StreamSupport.stream(allPages.spliterator(), true)
			.map(ServiceRequestEventMapper::map2)
			.filter(serviceRequestFilter)
			.collect(Collectors.toList());
		mappingStopwatch.stop();
		log.info("Mapping and filtering {} events: {} ms", serviceRequestList.size(), mappingStopwatch.elapsed(TimeUnit.MILLISECONDS));
		
		// Measure sorting
		if(orderBy != null && orderBy.length > 0) {
			Stopwatch sortStopwatch = Stopwatch.createStarted();
			serviceRequestList.sort(new ServiceRequestComparator(orderBy));
			sortStopwatch.stop();
			log.info("Sorting {} service requests: {} ms", serviceRequestList.size(), sortStopwatch.elapsed(TimeUnit.MILLISECONDS));
		}

		// Measure pagination calculation
		Stopwatch paginationStopwatch = Stopwatch.createStarted();
		int totalElements = serviceRequestList.size();
		int totalPages = (int) Math.ceil((double) totalElements / pageSize);

		List<ServiceRequest> currentPage = serviceRequestList.stream()
			.skip((long) (pageNumber - 1) * pageSize)
			.limit(pageSize)
			.collect(Collectors.toList());
		paginationStopwatch.stop();
		log.info("Pagination calculation: {} ms", paginationStopwatch.elapsed(TimeUnit.MILLISECONDS));
		
		RequestList<ServiceRequest> requestList = new RequestList<>();
		requestList.setCurrentPage(pageNumber);
		requestList.setList(currentPage);
		requestList.setPageSize(pageSize);
		requestList.setTotalPages(totalPages);
		requestList.setTotalElements(Long.valueOf(serviceRequestList.size()));
		
		totalStopwatch.stop();
		log.info("getServiceRequestByFilterAndInternalFilter total: {} ms (page {}, {} elements)", 
			totalStopwatch.elapsed(TimeUnit.MILLISECONDS), pageNumber, currentPage.size());
		
		return requestList;
	}

	protected <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) {
		if (c == null)
			return Collections.emptyList();
		List<T> list = new ArrayList<T>(c);
		if (pageSize == null || pageSize <= 0 || pageSize > list.size())
			pageSize = list.size();
		int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
		List<List<T>> pages = new ArrayList<List<T>>(numPages);
		for (int pageNum = 1; pageNum <= numPages; pageNum++) {
			if(pageNum == 1) {
				// cumulocity starts pages with 1, this is dummy page 0 contains same elements as page 1
				pages.add(list.subList((pageNum -1) * pageSize, Math.min(pageNum * pageSize, list.size())));
			}
			pages.add(list.subList((pageNum -1) * pageSize, Math.min(pageNum * pageSize, list.size())));
		}
		return pages;
	}

	private PagedEventCollectionRepresentation getPagedEventCollection(EventCollection eventList, Integer pageSize,
			Boolean withTotalPages) {
		QueryParam queryParamWithTotalElements = new QueryParam(StatisticsParam.WITH_TOTAL_ELEMENTS, "true");
		
		if (pageSize != null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize, queryParam, queryParamWithTotalElements);
			return pagedEvent;
		}

		if (pageSize == null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(queryParam, queryParamWithTotalElements);
			return pagedEvent;
		}

		if (pageSize != null && withTotalPages == null) {
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize, queryParamWithTotalElements);
			return pagedEvent;
		}

		PagedEventCollectionRepresentation pagedEvent = eventList.get(queryParamWithTotalElements);
		return pagedEvent;
	}

	@Override
	public int uploadAttachment(Resource resource, String contentType, byte[] bytes, String serviceRequestId, boolean overwrites) {
		log.info("uploadAttachment(filename: {}, ContentType: {})", resource.getFilename(), contentType);
		BinaryInfo binaryInfo = new BinaryInfo();
		binaryInfo.setName(resource.getFilename());
		binaryInfo.setType(contentType);
		return eventAttachmentApi.uploadEventAttachment(binaryInfo, resource, serviceRequestId, overwrites);
	}

	@Override
	public EventAttachment downloadAttachment(String serviceRequestId) {
		log.info("downloadAttachment(serviceRequestId: {})", serviceRequestId);
		return eventAttachmentApi.downloadEventAttachment(serviceRequestId);
	}
	
	@Override
	public ServiceRequest updateServiceRequestStatus(String id, ServiceRequestStatus status) {
		log.debug("updateServiceRequestStatus(id: {})", status.getId());
		
		ServiceRequestPatchRqBody serviceRequestPatch = new ServiceRequestPatchRqBody();
		serviceRequestPatch.setStatus(status);
		ServiceRequest updatedServiceRequest = updateServiceRequest(id, serviceRequestPatch);
		return updatedServiceRequest;
	}

	@Override
	public ServiceRequest updateServiceRequestActive(String id, Boolean isActive) {
		log.debug("updateServiceRequestActive(id: {}, isActive: {})",id, isActive);
		
		ServiceRequestPatchRqBody serviceRequestPatch = new ServiceRequestPatchRqBody();
		serviceRequestPatch.setIsActive(isActive);
		return updateServiceRequest(id, serviceRequestPatch);
	}

	@Override
	public ServiceRequest addAlarmRefToServiceRequest(String serviceRequestId, @Valid ServiceRequestDataRef alarmRef) {
		log.debug("addAlarmRefToServiceRequest(serviceRequestId: {}, alarmRef: {})", serviceRequestId, alarmRef);
		EventRepresentation event = eventApi.getEvent(GId.asGId(serviceRequestId));
		if( event == null) {
			log.error("Service Request with id {} not found!", serviceRequestId);
			return null;
		}

		updateAlarmDataRef(alarmRef);

		ServiceRequestEventMapper eventMapper = new ServiceRequestEventMapper(event);
		eventMapper.addAlarmRef(alarmRef);

		ServiceRequestEventMapper updateEventMapper = ServiceRequestEventMapper.map2Alarm(serviceRequestId, eventMapper.getAlarmRefList());
		EventRepresentation updatedEvent = eventApi.update(updateEventMapper.getEvent());
		ServiceRequest updatedServiceRequest = ServiceRequestEventMapper.map2(updatedEvent);

		createSystemComment("Alarm " + alarmRef.getId() + " reference added", updatedServiceRequest);

		// Update alarm
		List<ServiceRequestStatusConfig> statusList = serviceRequestStatusConfigService.getStatusList();
			
		ServiceRequestStatusConfig srStatus = null;
		for(ServiceRequestStatusConfig srStatusConfig: statusList) {
			if(srStatusConfig.getId().equals(updatedServiceRequest.getStatus().getId())) {
				srStatus = srStatusConfig;
			}
		}

		updateAlarm(updatedServiceRequest, alarmRef, srStatus);

		return updatedServiceRequest;
	}
	
	@Override
	public ServiceRequest addEventRefToServiceRequest(String serviceRequestId, @Valid ServiceRequestDataRef eventRef) {
		log.debug("addEventRefToServiceRequest(serviceRequestId: {}, eventRef: {})", serviceRequestId, eventRef);
		EventRepresentation event = eventApi.getEvent(GId.asGId(serviceRequestId));
		if( event == null) {
			log.error("Service Request with id {} not found!", serviceRequestId);
			return null;
		}

		updateEventDataRef(eventRef);

		// Update service request with event reference
		ServiceRequestEventMapper eventMapper = new ServiceRequestEventMapper(event);
		eventMapper.addEventRef(eventRef);

		ServiceRequestEventMapper updateEventMapper = ServiceRequestEventMapper.map2Event(serviceRequestId, eventMapper.getEventRefList());
		EventRepresentation updatedEvent = eventApi.update(updateEventMapper.getEvent());
		ServiceRequest updatedServiceRequest = ServiceRequestEventMapper.map2(updatedEvent);

		createSystemComment("Event " + eventRef.getId() + " reference added", updatedServiceRequest);

		// Update Event
		updateEvent(serviceRequestId, eventRef);

		return updatedServiceRequest;
	}
	
	private void updateAlarm(ServiceRequest serviceRequest, ServiceRequestDataRef alarmRef, ServiceRequestStatusConfig srStatus) {
		serviceRequestUpdateService.updateAlarm(serviceRequest, alarmRef, srStatus, userContextService.getContext(), contextService.getContext());
	}

	private void updateEvent(String serviceRequestId, ServiceRequestDataRef eventRef) {
		EventMapper eventMapper2 = EventMapper.map2(serviceRequestId, eventRef);
		eventApi.update(eventMapper2.getEvent());
	}

	private void createCommentForStatusChange(String prefix, ServiceRequest serviceRequest) {
		serviceRequestUpdateService.createCommentForStatusChange(prefix, serviceRequest, contextService.getContext());
	}
	
	private void createSystemComment(String text, ServiceRequest serviceRequest) {
		if(serviceRequest == null) {
			log.warn("Couldn't add system comment, service request is null!");
			return;
		}
		ServiceRequestCommentRqBody comment = new ServiceRequestCommentRqBody();
		comment.setText(text);
		comment.setType(ServiceRequestCommentType.SYSTEM);
		serviceRequestCommentService.createComment(serviceRequest.getSource().getId(), serviceRequest.getId(), comment, null);
	}
	
	private void updateAllCommentsToClosed(ServiceRequest serviceRequest) {
		serviceRequestUpdateService.updateAllCommentsToClosed(serviceRequest, contextService.getContext());
	}

	private void updateServiceRequestCounter(ServiceRequest serviceRequest, List<String> excludeList) {
		serviceRequestUpdateService.updateServiceRequestCounter(serviceRequest, excludeList, contextService.getContext());
	}

	private void updateAlarmDataRef(ServiceRequestDataRef alarmRef) {
		if(alarmRef == null || alarmRef.getUri() != null) {
			return;
		}
		
		AlarmRepresentation alarm = null;
		try{
			alarm = alarmApi.getAlarm(GId.asGId(alarmRef.getId()));
		}catch(Exception e){
			log.error("Fetching alarm failed!", e);
		}
		if(alarm != null) {
			alarmRef.setUri(alarm.getSelf());				
		}
	}

	private void updateEventDataRef(ServiceRequestDataRef eventRef) {
		if(eventRef == null || eventRef.getUri() != null) {
			return;
		}
		
		EventRepresentation event = null;
		try{
			event = eventApi.getEvent(GId.asGId(eventRef.getId()));
		}catch(Exception e){
			log.error("Fetching event failed!", e);
		}
		if(event != null) {
			eventRef.setUri(event.getSelf());				
		}
	}
}

