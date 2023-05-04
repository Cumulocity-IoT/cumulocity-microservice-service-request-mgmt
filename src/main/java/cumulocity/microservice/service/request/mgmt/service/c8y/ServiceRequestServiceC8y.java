package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestServiceC8y implements ServiceRequestService {
	
	private EventApi eventApi;
	
	private EventAttachmentApi eventAttachmentApi;
	
	@Autowired
	public ServiceRequestServiceC8y(EventApi eventApi, EventAttachmentApi eventAttachmentApi) {
		this.eventApi = eventApi;
		this.eventAttachmentApi = eventAttachmentApi;
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
	public ServiceRequest updateServiceRequest(String id, ServiceRequestPatchRqBody serviceRequest) {
		ServiceRequestEventMapper eventMapper = ServiceRequestEventMapper.map2(id, serviceRequest);
		EventRepresentation updatedEvent = eventApi.update(eventMapper.getEvent());
		return eventMapper.map2(updatedEvent);
	}
	
	@Override
	public ServiceRequest getServiceRequestById(String id) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(id));
		return ServiceRequestEventMapper.map2(event);
	}

	@Override
	public RequestList<ServiceRequest> getAllServiceRequestByFilter(String sourceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages) {
		log.info("find all service requests!");
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		if(sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(Boolean.TRUE).setWithSourceDevices(Boolean.FALSE);
		}

		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public RequestList<ServiceRequest> getActiveServiceRequestByFilter(String sourceId, Integer pageSize, Integer pageNumber, Boolean withTotalPages) {
		log.info("find all active service requests!");
		EventFilterExtend filter = new EventFilterExtend();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE);
		filter.byFragmentValue(Boolean.TRUE.toString());
		if(sourceId != null) {
			filter.bySource(GId.asGId(sourceId));
			filter.setWithSourceAssets(Boolean.TRUE).setWithSourceDevices(Boolean.FALSE);
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
			Object externalId = eventRepresentation.get(ServiceRequestEventMapper.SR_EXTERNAL_ID);
			if(assigned && externalId != null) {
				ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
				serviceRequestList.add(sr);
			}else if(!assigned && externalId == null) {
				ServiceRequest sr = ServiceRequestEventMapper.map2(eventRepresentation);
				serviceRequestList.add(sr);
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

	private RequestList<ServiceRequest> getServiceRequestByFilter(EventFilter filter, Integer pageSize, Integer pageNumber, Boolean withTotalPages) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);
				
		//TODO return specific page, eventList.getPage(null, 0, 0)
		PagedEventCollectionRepresentation pagedCollection = getPagedEventCollection(eventList, pageSize, withTotalPages);
		if(pageNumber != null) {
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


	
}
