package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.event.PagedEventCollectionRepresentation;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

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
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());
		ServiceRequest newServiceRequest = ServiceRequestEventMapper.map2(createdEvent);
		return newServiceRequest;
	}

	@Override
	public ServiceRequest getServiceRequestById(Integer id) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(id));
		return ServiceRequestEventMapper.map2(event);
	}

	@Override
	public ServiceRequest getServiceRequestByExternalId(String externalId) {
		return null;
	}

	@Override
	public RequestList<ServiceRequest> getServiceRequestByFilter(String deviceId, Integer pageSize) {
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.bySource(GId.asGId(deviceId));
		EventCollection eventList = eventApi.getEventsByFilter(filter);
		
		PagedEventCollectionRepresentation pagedEvent = eventList.get(pageSize);
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

	@Override
	public void deleteServiceRequest(Integer id) {
		EventRepresentation eventRepresentation = new EventRepresentation();
		eventRepresentation.setId(GId.asGId(id));
		eventApi.delete(eventRepresentation);
	}

	
}
