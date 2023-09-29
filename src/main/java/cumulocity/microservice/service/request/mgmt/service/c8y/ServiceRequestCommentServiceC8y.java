package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestCommentServiceC8y implements ServiceRequestCommentService {

	private EventApi eventApi;

	private EventAttachmentApi eventAttachmentApi;

	@Autowired
	public ServiceRequestCommentServiceC8y(EventApi eventApi, EventAttachmentApi eventAttachmentApi) {
		super();
		this.eventApi = eventApi;
		this.eventAttachmentApi = eventAttachmentApi;
	}

	@Override
	public ServiceRequestComment createComment(String deviceId, String serviceRequestId,
			ServiceRequestCommentRqBody serviceRequestComment, String owner) {
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
		EventFilter filter = createCommentEventFilter(serviceRequestId);
		return getServiceRequestByFilter(filter, pageSize, pageNumber, withTotalPages);
	}

	@Override
	public List<ServiceRequestComment> getCompleteUserCommentListByServiceRequest(String serviceRequestId) {
		log.info("fetch comments for service request {}", serviceRequestId);
		EventFilter filter = createCommentEventFilter(serviceRequestId);

		Predicate<ServiceRequestComment> filterPredicate = srcomment -> srcomment.getType()
				.equals(ServiceRequestCommentType.USER);

		List<ServiceRequestComment> serviceRequestCommentByFilterAndInternalFilter = getServiceRequestCommentByFilterAndInternalFilter(
				filter, filterPredicate);
		log.info("return list of comments, size {}", serviceRequestCommentByFilterAndInternalFilter.size());
		return serviceRequestCommentByFilterAndInternalFilter;
	}

	@Override
	public List<ServiceRequestComment> getCompleteCommentListByServiceRequest(String serviceRequestId) {
		EventFilter filter = createCommentEventFilter(serviceRequestId);

		List<ServiceRequestComment> serviceRequestCommentByFilterAndInternalFilter = getServiceRequestCommentByFilterAndInternalFilter(
				filter, null);
		return serviceRequestCommentByFilterAndInternalFilter;
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

	private RequestList<ServiceRequestComment> getServiceRequestByFilter(EventFilter filter, Integer pageSize,
			Integer pageNumber, Boolean withTotalPages) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);

		PagedEventCollectionRepresentation pagedCollection = getPagedEventCollection(eventList, pageSize,
				withTotalPages);
		if (pageNumber != null) {
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

	private List<ServiceRequestComment> getServiceRequestCommentByFilterAndInternalFilter(EventFilter filter,
			Predicate<ServiceRequestComment> serviceRequestFilter) {
		EventCollection eventList = eventApi.getEventsByFilter(filter);

		Iterable<EventRepresentation> allPages = eventList.get(2000).allPages();
		List<ServiceRequestComment> serviceRequestCommentList = new ArrayList<>();
		for (Iterator<EventRepresentation> iterator = allPages.iterator(); iterator.hasNext();) {
			EventRepresentation eventRepresentation = iterator.next();
			ServiceRequestComment srComment = ServiceRequestCommentEventMapper.map2(eventRepresentation);
			if (serviceRequestFilter == null || serviceRequestFilter.test(srComment)) {
				serviceRequestCommentList.add(srComment);
			}
		}

		return serviceRequestCommentList;
	}
	
	private EventFilter createCommentEventFilter(String serviceRequestId) {
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestCommentEventMapper.EVENT_TYPE);
		filter.byFragmentType(ServiceRequestCommentEventMapper.SR_ID);
		filter.byFragmentValue(serviceRequestId);
		return filter;
	}

	@Override
	public void uploadAttachment(Resource resource, String contentType, byte[] bytes, String commentId) {
		log.info("Attachment info: Filename: {}, ContentType: {}", resource.getFilename(), contentType);
		BinaryInfo binaryInfo = new BinaryInfo();
		binaryInfo.setName(resource.getFilename());
		binaryInfo.setType(contentType);
		eventAttachmentApi.uploadEventAttachment(binaryInfo, resource, commentId);
	}

	@Override
	public EventAttachment downloadAttachment(String commentId) {
		log.info("Attachment info: Service request comment {}", commentId);
		return eventAttachmentApi.downloadEventAttachment(commentId);
	}

}
