package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.PagingParam;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.event.PagedEventCollectionRepresentation;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPatchRqBody;
import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestServiceStandard implements ServiceRequestService {
	
	private EventApi eventApi;
	
	
	public ServiceRequestServiceStandard(EventApi eventApi) {
		this.eventApi = eventApi;
	}

	@Override
	public ServiceRequest createServiceRequest(ServiceRequestPostRqBody serviceRequestRqBody, String owner) {
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(serviceRequestRqBody);
		eventMapper.setOwner(owner);
		eventMapper.setIsActive(Boolean.TRUE);
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());
		ServiceRequest newServiceRequest = ServiceRequestEventMapper.map2(createdEvent);
		return newServiceRequest;
	}

	@Override
	public ServiceRequest updateServiceRequest(Long id, ServiceRequestPatchRqBody serviceRequest) {
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(serviceRequest);
		eventMapper.setId(id);
		EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
		return eventMapper.map2(updatedEvent);
	}
	
	@Override
	public ServiceRequest getServiceRequestById(Long id) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(id));
		return ServiceRequestEventMapper.map2(event);
	}

	@Override
	public ServiceRequest getServiceRequestByExternalId(String externalId) {
		return null;
	}

	@Override
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String deviceId, Integer pageSize, Boolean withTotalPages) {
		log.info("find all service requests!");
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		if(deviceId != null) {
			filter.bySource(GId.asGId(deviceId));			
		}
		return getServiceRequestByFilter(filter, pageSize, withTotalPages);
	}

	@Override
	public RequestList<ServiceRequest> getActiveServiceRequestByFilter(String deviceId, Integer pageSize, Boolean withTotalPages) {
		log.info("find all active service requests!");
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE);
		if(deviceId != null) {
			filter.bySource(GId.asGId(deviceId));			
		}
		return getServiceRequestByFilter(filter, pageSize, withTotalPages);
	}
	
	@Override
	public void deleteServiceRequest(Long id) {
		EventRepresentation eventRepresentation = new EventRepresentation();
		eventRepresentation.setId(GId.asGId(id));
		eventApi.delete(eventRepresentation);
	}

	private RequestList<ServiceRequest> getServiceRequestByFilter(EventFilter filter, Integer pageSize, Boolean withTotalPages) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);
		
		PagedEventCollectionRepresentation pagedEvent = getPagedEventCollection(eventList, pageSize, withTotalPages);
		PageStatisticsRepresentation pageStatistics = pagedEvent.getPageStatistics();
		List<EventRepresentation> events = pagedEvent.getEvents();
		
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
	
	private PagedEventCollectionRepresentation getPagedEventCollection(EventCollection eventList, Integer pageSize, Boolean withTotalPages) {
		if(pageSize != null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize, queryParam);
			return pagedEvent;
		}
		
		if(pageSize == null && withTotalPages != null) {
			QueryParam queryParam = new QueryParam(PagingParam.WITH_TOTAL_PAGES, withTotalPages.toString());
			PagedEventCollectionRepresentation pagedEvent = eventList.get(queryParam);
			return pagedEvent;
		}

		if(pageSize != null && withTotalPages == null) {
			PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize);
			return pagedEvent;
		}
		
		PagedEventCollectionRepresentation pagedEvent = eventList.get();
		return pagedEvent;
	}
	
}
