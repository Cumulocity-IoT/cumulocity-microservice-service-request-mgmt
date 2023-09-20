package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestServiceC8y implements ServiceRequestService {
	
	private EventApi eventApi;

	private AlarmApi alarmApi;

	private EventAttachmentApi eventAttachmentApi;

	private InventoryApi inventoryApi;
	
	private ServiceRequestStatusService serviceRequestStatusService;
	
	private ServiceRequestCommentService serviceRequestCommentService;
			
	@Autowired
	public ServiceRequestServiceC8y(EventApi eventApi, EventAttachmentApi eventAttachmentApi, AlarmApi alarmApi,
			InventoryApi inventoryApi, ServiceRequestStatusService serviceRequestStatusService, ServiceRequestCommentService serviceRequestCommentService) {
		this.eventApi = eventApi;
		this.eventAttachmentApi = eventAttachmentApi;
		this.alarmApi = alarmApi;
		this.inventoryApi = inventoryApi;
		this.serviceRequestStatusService = serviceRequestStatusService;
		this.serviceRequestCommentService = serviceRequestCommentService;
	}

	@Override
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequestRqBody, String owner) {
		Optional<ServiceRequestStatus> srStatus = serviceRequestStatusService.getStatus(serviceRequestRqBody.getStatus().getId());
		String srStatusIdExclude = null;
		if(srStatus.isEmpty()) {
			log.warn("Status {} is not part of the configured status list!", serviceRequestRqBody.getStatus().toString());
		}else {
			srStatusIdExclude = srStatus.get().getExcludeForCounter() != null ? srStatus.get().getId(): null;
		}
		
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(serviceRequestRqBody);
		eventMapper.setOwner(owner);
		eventMapper.setIsActive(Boolean.TRUE);
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());
		ServiceRequest newServiceRequest = ServiceRequestEventMapper.map2(createdEvent);

		//track status changes as system comment
		createCommentForStatusChange("Initial Status", newServiceRequest);
		
		// Alarm status transition
		if(srStatus.isPresent() && newServiceRequest.getAlarmRef() != null) {
			updateAlarm(newServiceRequest, srStatus.get());
		}
		// Update Managed Object
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(newServiceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);

		if(srStatusIdExclude == null) {
			moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()));
		}else {
			moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), srStatusIdExclude);
		}

		inventoryApi.update(moMapper.getManagedObjectRepresentation());

		return newServiceRequest;
	}

	@Override
	public ServiceRequest updateServiceRequest(String id, ServiceRequestPatchRqBody serviceRequest) {
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(id, serviceRequest);
		ServiceRequest updatedServiceRequest = null;
		String srStatusIdExclude = null;
		
		if(serviceRequest.getStatus() == null) {
			log.debug("Service Request update without status changes!");
			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
			updatedServiceRequest = eventMapper.map2(updatedEvent);
		}else {
			log.debug("Service Request update with status changes!");
			Optional<ServiceRequestStatus> srStatus = serviceRequestStatusService.getStatus(serviceRequest.getStatus().getId());

			if(srStatus.isEmpty()) {
				log.warn("Status {} is not part of the configured status list!");
			}else {
				srStatusIdExclude = srStatus.get().getExcludeForCounter() != null ? srStatus.get().getId(): null;
			}
			
			ServiceRequest originalServiceRequest = getServiceRequestById(id);

			
			//Closing transition
			if(srStatus.get().getIsClosedTransition() != null) {
				eventMapper.setIsClosed(Boolean.TRUE);
			}
			
			EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());

			updatedServiceRequest = eventMapper.map2(updatedEvent);

			//track status changes as system comment
			createCommentForStatusChange("Updated Status", updatedServiceRequest);

			// Alarm status transition
			if(!originalServiceRequest.getStatus().getId().equals(updatedServiceRequest.getStatus().getId())) {
				updateAlarm(updatedServiceRequest, srStatus.get());
			}			
		}
		
		log.debug("Update Managed Object"); 
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(updatedServiceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);
		if(srStatusIdExclude == null) {
			moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()));
		}else {
			moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), srStatusIdExclude);
		}
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
		log.info("find all service requests!");
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
		log.info("find all active service requests!");
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
		log.info("find all active service requests!");
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE);
		filter.byFragmentValue(Boolean.TRUE.toString());

		EventCollection eventList = eventApi.getEventsByFilter(filter);

		Iterable<EventRepresentation> allPages = eventList.get(2000).allPages();
		List<ServiceRequest> serviceRequestList = new ArrayList<>();
		for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
			EventRepresentation eventRepresentation = iterator.next();
			if(assigned == null) {
				ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
				serviceRequestList.add(sr);
			}else {
				Object externalId = eventRepresentation.get(ServiceRequestEventMapper.SR_EXTERNAL_ID);
				if (assigned && externalId != null) {
					ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
					serviceRequestList.add(sr);
				} else if (!assigned && externalId == null) {
					ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
					serviceRequestList.add(sr);
				}
			}
		}
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
		log.info("Attachment info: Filename: {}, ContentType: {}", resource.getFilename(), contentType);
		BinaryInfo binaryInfo = new BinaryInfo();
		binaryInfo.setName(resource.getFilename());
		binaryInfo.setType(contentType);
		eventAttachmentApi.uploadEventAttachment(binaryInfo, resource, serviceRequestId);
	}

	@Override
	public EventAttachment downloadAttachment(String serviceRequestId) {
		log.info("Attachment info: Service request {}", serviceRequestId);
		return eventAttachmentApi.downloadEventAttachment(serviceRequestId);
	}
	
	@Override
	public ServiceRequest updateServiceRequestStatus(String id, ServiceRequestStatus status) {
		log.info("Change status of service request to id={}, name={}", status.getId(), status.getName());
		
		ServiceRequestPatchRqBody serviceRequestPatch = new ServiceRequestPatchRqBody();
		serviceRequestPatch.setStatus(status);
		ServiceRequest updatedServiceRequest = updateServiceRequest(id, serviceRequestPatch);
		return updatedServiceRequest;
	}

	@Override
	public ServiceRequest updateServiceRequestActive(String id, Boolean isActive) {
		log.info("Change active status of service request to isActive={}", isActive);
		
		ServiceRequestPatchRqBody serviceRequestPatch = new ServiceRequestPatchRqBody();
		serviceRequestPatch.setIsActive(isActive);
		return updateServiceRequest(id, serviceRequestPatch);
	}
	
	private void updateAlarm(ServiceRequest serviceRequest, ServiceRequestStatus srStatus) {
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
		ServiceRequestCommentRqBody comment = new ServiceRequestCommentRqBody();
		comment.setText(prefix + ", Id: " + serviceRequest.getStatus().getId() + ", Name: " + serviceRequest.getStatus().getName());
		comment.setType(ServiceRequestCommentType.SYSTEM);
		serviceRequestCommentService.createComment(serviceRequest.getSource().getId(), serviceRequest.getId(), comment, null);
	}
}
