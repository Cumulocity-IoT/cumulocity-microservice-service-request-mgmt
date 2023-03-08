package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;

@Service
public class ServiceRequestCommentServiceC8y implements ServiceRequestCommentService {

	private EventApi eventApi;
	
	@Autowired
	public ServiceRequestCommentServiceC8y(EventApi eventApi) {
		super();
		this.eventApi = eventApi;
	}

	@Override
	public ServiceRequestComment createComment(String deviceId, String serviceRequestId, ServiceRequestCommentRqBody serviceRequestComment,
			String owner) {
		ServiceRequestCommentEventMapper eventMapper = ServiceRequestCommentEventMapper.map2(serviceRequestComment);
		eventMapper.setOwner(owner);
		eventMapper.setServiceRequestId(serviceRequestId);
		eventMapper.setSource(new ServiceRequestSource(deviceId));
		
		EventRepresentation createdEvent = eventApi.create(eventMapper.getEvent());

		return ServiceRequestCommentEventMapper.map2(createdEvent);
	}

	@Override
	public RequestList<ServiceRequestComment> getCommentListByFilter(String serviceRequestId, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages) {
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestCommentEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestCommentEventMapper.SR_ID);
		filter.byFragmentValue(serviceRequestId);
		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public ServiceRequestComment getCommentById(String id) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(id));
		return ServiceRequestCommentEventMapper.map2(event);
	}

	@Override
	public void deleteComment(String id) {
		EventRepresentation eventRepresentation = new EventRepresentation();
		eventRepresentation.setId(GId.asGId(id));
		eventApi.delete(eventRepresentation);
	}

	@Override
	public ServiceRequestComment updateComment(String id, ServiceRequestCommentRqBody serviceRequestComment) {
		ServiceRequestCommentEventMapper eventMapper = ServiceRequestCommentEventMapper.map2(id, serviceRequestComment);
		EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
		return ServiceRequestCommentEventMapper.map2(updatedEvent);
	}
	
	private RequestList<ServiceRequestComment> getServiceRequestByFilter(EventFilter filter, Integer pageSize, Integer pageNumber, Boolean withTotalPages) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);
				
		PagedEventCollectionRepresentation pagedCollection = getPagedEventCollection(eventList, pageSize, withTotalPages);
		if(pageNumber != null) {
			pagedCollection = eventList.getPage(pagedCollection, pageNumber, pageSize);	
		}
		
		PageStatisticsRepresentation pageStatistics = pagedCollection.getPageStatistics();
		List<EventRepresentation> events = pagedCollection.getEvents();
		
		List<ServiceRequestComment> serviceRequestCommentList = events.stream().map(event -> {
			return ServiceRequestCommentEventMapper.map2(event);
		}).collect(Collectors.toList());
		
		RequestList<ServiceRequestComment> requestList = new RequestList<>();
		requestList.setCurrentPage(pageStatistics.getCurrentPage());
		requestList.setList(serviceRequestCommentList);
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
