package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PagingParam;
import com.cumulocity.sdk.client.QueryParam;
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
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusConfigService;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestEventMapper.SyncStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestServiceC8y implements ServiceRequestService {
	
	private EventApi eventApi;

	private AlarmApi alarmApi;

	private EventAttachmentApi eventAttachmentApi;

	private InventoryApi inventoryApi;
	
	private ServiceRequestStatusConfigService serviceRequestStatusConfigService;
	
	private ServiceRequestCommentService serviceRequestCommentService;
			
	@Autowired
	public ServiceRequestServiceC8y(EventApi eventApi, EventAttachmentApi eventAttachmentApi, AlarmApi alarmApi,
			InventoryApi inventoryApi, ServiceRequestStatusConfigService serviceRequestStatusConfigService, ServiceRequestCommentService serviceRequestCommentService) {
		this.eventApi = eventApi;
		this.eventAttachmentApi = eventAttachmentApi;
		this.alarmApi = alarmApi;
		this.inventoryApi = inventoryApi;
		this.serviceRequestStatusConfigService = serviceRequestStatusConfigService;
		this.serviceRequestCommentService = serviceRequestCommentService;
	}

	@Override
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequestRqBody, String owner) {
		log.info("createServiceRequest(serviceRequestRqBody {}, owner {})", serviceRequestRqBody.toString(), owner);

		List<ServiceRequestStatusConfig> statusList = serviceRequestStatusConfigService.getStatusList();
		List<String> excludeList = new ArrayList<>();
		
		ServiceRequestStatusConfig srStatus = null;

		for(ServiceRequestStatusConfig srStatusConfig: statusList) {
			if(Boolean.TRUE.equals(srStatusConfig.getIsExcludeForCounter())) {
				excludeList.add(srStatusConfig.getId());
			}
			if(srStatusConfig.getId().equals(serviceRequestRqBody.getStatus().getId())) {
				srStatus = srStatusConfig;
			}
		}
		
		if(srStatus == null) {
			log.error("Status {} is not part of the configured status list! Service Reqeust can't be updated!!!", serviceRequestRqBody.getStatus().toString());
			return null;
		}
		
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(serviceRequestRqBody);
		eventMapper.setOwner(owner);
		eventMapper.setIsActive(Boolean.TRUE);
		eventMapper.setSyncStatus(SyncStatus.NEW);
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());
		ServiceRequest newServiceRequest = ServiceRequestEventMapper.map2(createdEvent);

		//track status changes as system comment
		createCommentForStatusChange("Initial Status", newServiceRequest);
		
		// Alarm status transition
		if(newServiceRequest.getAlarmRef() != null) {
			updateAlarm(newServiceRequest, srStatus);
		}
		
		// Update Managed Object
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(newServiceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);

		moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), excludeList);
		inventoryApi.update(moMapper.getManagedObjectRepresentation());

		return newServiceRequest;
	}

	@Override
	public ServiceRequest updateServiceRequest(String id, ServiceRequestPatchRqBody serviceRequest) {
		log.info("updateServiceRequest(id {}, serviceRequestBody {})", id, serviceRequest.toString());
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(id, serviceRequest);
		if(serviceRequest.getExternalId() != null) {
			// if external ID is set, the sync status changes to active
			eventMapper.setSyncStatus(SyncStatus.ACTIVE);
		}
		ServiceRequest updatedServiceRequest = null;
		List<String> excludeList = new ArrayList<>();
		
		if(serviceRequest.getStatus() == null) {
			log.debug("Service Request update without status changes!");
			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
			updatedServiceRequest = eventMapper.map2(updatedEvent);
		}else {
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
			
			ServiceRequest originalServiceRequest = getServiceRequestById(id);
			
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
				log.info("Active status was changed from true to false!");
				eventMapper.setIsClosed(Boolean.TRUE);
				eventMapper.setSyncStatus(SyncStatus.STOP);
			}
			
			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());

			updatedServiceRequest = eventMapper.map2(updatedEvent);

			if(!originalServiceRequest.getStatus().getId().equals(updatedServiceRequest.getStatus().getId())) {
				//track status changes as system comment
				createCommentForStatusChange("Updated Status", updatedServiceRequest);

				// Alarm status transition
				updateAlarm(updatedServiceRequest, srStatus);
			}
			
			//if service request is closed all comments must also be set to closed
			if(updatedServiceRequest.getIsClosed()) {
				updateAllCommentsToClosed(updatedServiceRequest);
			}

		}
		
		log.debug("Update Managed Object"); 
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(updatedServiceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);
		moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), excludeList);

		inventoryApi.update(moMapper.getManagedObjectRepresentation());
		return updatedServiceRequest;
	}

	@Override
	public ServiceRequest getServiceRequestById(String id) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(id));
		return ServiceRequestEventMapper.map2(event);
	}

	@Override
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String sourceId, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages, String[] statusList, Long[] priorityList, String[] orderBy) {
		log.info("getAllServiceRequestByFilter(sourceId: {}, pageSize: {}, pageNumber: {}, withTotalPages: {}, statusList: {}, priorityList: {}, orderBy: {})", sourceId, pageSize, pageNumber, withTotalPages, statusList, priorityList, orderBy);
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		if (sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(Boolean.TRUE).setWithSourceDevices(Boolean.FALSE);
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
			Integer pageNumber, Boolean withTotalPages, String[] statusList, Long[] priorityList, String[] orderBy) {
		log.info("getActiveServiceRequestByFilter(sourceId: {}, pageSize: {}, pageNumber: {}, withTotalPages: {}, statusList: {}, priorityList: {}, orderBy: {})", sourceId, pageSize, pageNumber, withTotalPages, statusList, priorityList, orderBy);
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE);
		filter.byFragmentValue(Boolean.TRUE.toString());
		if (sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(Boolean.TRUE).setWithSourceDevices(Boolean.FALSE);
		}
		
		boolean isStatusFilter = ArrayUtils.isNotEmpty(statusList);
		boolean isPriorityFilter = ArrayUtils.isNotEmpty(priorityList);
		if(isStatusFilter || isPriorityFilter) {
			Predicate<ServiceRequest> filterPredicate = sr -> sr.getStatus() != null && sr.getPriority() != null;
			
			if(isStatusFilter) {
				filterPredicate = filterPredicate.and(sr -> ArrayUtils.contains(statusList, sr.getStatus().getId()));
			}
			
			if(isPriorityFilter) {
				filterPredicate = filterPredicate.and(sr -> ArrayUtils.contains(priorityList, sr.getPriority().getOrdinal()));
			}
						
			return getServiceRequestByFilterAndInternalFilter(filter, filterPredicate, pageSize, pageNumber, withTotalPages, orderBy);
		}
		
		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public List<ServiceRequest> getCompleteActiveServiceRequestByFilter(Boolean assigned) {
		log.info("getCompleteActiveServiceRequestByFilter(assigned: {})", assigned);
		Stopwatch stopwatch = Stopwatch.createStarted();
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_SYNC_STATUS);
		if(assigned != null && assigned) {
			// service request which must be updated
			filter.byFragmentValue(String.valueOf(SyncStatus.ACTIVE.name()));
		}else if(assigned != null && !assigned) {
			// service request which are new
			filter.byFragmentValue(String.valueOf(SyncStatus.NEW.name()));
		}
		EventCollection eventList = eventApi.getEventsByFilter(filter);
		Iterable<EventRepresentation> allPages = eventList.get(2000).allPages();
		List<ServiceRequest> serviceRequestList = new ArrayList<>();
		for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
			EventRepresentation eventRepresentation = iterator.next();
			if(assigned == null) {
				ServiceRequestEventMapper eventMapper = new ServiceRequestEventMapper(eventRepresentation);
				SyncStatus syncStatus = eventMapper.getSyncStatus();
				if(syncStatus.equals(SyncStatus.NEW) || syncStatus.equals(SyncStatus.ACTIVE)) {
					ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
					serviceRequestList.add(sr);
				}
			}else {
				ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);				
				serviceRequestList.add(sr);
			}
		}
		stopwatch.stop();
		long seconds = stopwatch.elapsed(TimeUnit.SECONDS);
		log.info("getCompleteActiveServiceRequestByFilter: return list.size {} in seconds {}", serviceRequestList.size(), seconds);
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
		return requestList;
	}

	protected RequestList<ServiceRequest> getAllActiveEventsBySource(GId sourceId) {
		EventFilter filter = new EventFilter();
		filter.bySource(sourceId).byFragmentType(ServiceRequestEventMapper.SR_ACTIVE).byFragmentValue(Boolean.TRUE.toString());
		return getServiceRequestByFilter(filter, 2000, null, null);
	}

	private RequestList<ServiceRequest> getServiceRequestByFilterAndInternalFilter(EventFilter filter,
			Predicate<ServiceRequest> serviceRequestFilter, Integer pageSize, Integer pageNumber,
			Boolean withTotalPages, final String[] orderBy) {
		
		pageNumber = pageNumber != null ? pageNumber : 0;
		pageSize = pageSize != null ? pageSize : 5;
		EventCollection eventList = eventApi.getEventsByFilter(filter);

		Iterable<EventRepresentation> allPages = eventList.get(2000).allPages();
		List<ServiceRequest> serviceRequestList = new ArrayList<>();
		for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
			EventRepresentation eventRepresentation = iterator.next();
			ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
			if (serviceRequestFilter.test(sr)) {
				serviceRequestList.add(sr);
			}
		}
		
		if(orderBy != null && orderBy.length > 0) {
			ServiceRequestComparator srComparator = new ServiceRequestComparator(orderBy);
			serviceRequestList.sort(srComparator);
		}

		List<List<ServiceRequest>> pages = getPages(serviceRequestList, pageSize);
		List<ServiceRequest> currentPage = new ArrayList<>();
		if(pages.size() > pageNumber) {
			currentPage = pages.get(pageNumber);
		}else {
			log.warn("Page number {} exceeds pages {} !", pageNumber, pages.size());
		}
		
		RequestList<ServiceRequest> requestList = new RequestList<>();
		requestList.setCurrentPage(pageNumber);
		requestList.setList(currentPage);
		requestList.setPageSize(pageSize);
		requestList.setTotalPages(pages.size());
		return requestList;
	}

	private <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) {
		if (c == null)
			return Collections.emptyList();
		List<T> list = new ArrayList<T>(c);
		if (pageSize == null || pageSize <= 0 || pageSize > list.size())
			pageSize = list.size();
		int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
		List<List<T>> pages = new ArrayList<List<T>>(numPages);
		for (int pageNum = 0; pageNum < numPages;)
			pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
		return pages;
	}

	private PagedEventCollectionRepresentation getPagedEventCollection(EventCollection eventList, Integer pageSize,
			Boolean withTotalPages) {
		if (pageSize != null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize, queryParam);
			return pagedEvent;
		}

		if (pageSize == null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(queryParam);
			return pagedEvent;
		}

		if (pageSize != null && withTotalPages == null) {
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize);
			return pagedEvent;
		}

		PagedEventCollectionRepresentation pagedEvent = eventList.get();
		return pagedEvent;
	}

	@Override
	public void uploadAttachment(Resource resource, String contentType, byte[] bytes, String serviceRequestId) {
		log.info("uploadAttachment(filename: {}, ContentType: {})", resource.getFilename(), contentType);
		BinaryInfo binaryInfo = new BinaryInfo();
		binaryInfo.setName(resource.getFilename());
		binaryInfo.setType(contentType);
		eventAttachmentApi.uploadEventAttachment(binaryInfo, resource, serviceRequestId);
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
	
	private void updateAlarm(ServiceRequest serviceRequest, ServiceRequestStatusConfig srStatus) {
		if(serviceRequest == null) {
			return;
		}
		
		CumulocityAlarmStatuses alarmStatus = srStatus.getAlarmStatusTransition() != null ? CumulocityAlarmStatuses.valueOf(srStatus.getAlarmStatusTransition()): null;

		if(alarmStatus == null) {
			return;
		}
		
		AlarmMapper alarmMapper = AlarmMapper.map2(serviceRequest, alarmStatus);
		if (alarmMapper != null) {
			AlarmRepresentation alarmRepresentation = alarmMapper.getAlarm();
			alarmApi.update(alarmRepresentation);
		}
	}
	
	private void createCommentForStatusChange(String prefix, ServiceRequest serviceRequest) {
		if(serviceRequest == null) {
			log.warn("Couldn't add system comment, service request is null!");
			return;
		}
		String text = prefix + ", Id: " + serviceRequest.getStatus().getId() + ", Name: " + serviceRequest.getStatus().getName();
		createSystemComment(text, serviceRequest);
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
		List<ServiceRequestComment> commentList = serviceRequestCommentService.getCompleteCommentListByServiceRequest(serviceRequest.getId());
		
		for(ServiceRequestComment comment: commentList) {
			ServiceRequestCommentRqBody commentUpdate = new ServiceRequestCommentRqBody();
			commentUpdate.setIsClosed(Boolean.TRUE);
			serviceRequestCommentService.updateComment(comment.getId(), commentUpdate);			
		}
	}
}
