package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.event.PagedEventCollectionRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestCommentRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestCommentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceRequestUpdateService {

	private EventApi eventApi;

	private AlarmApi alarmApi;

	private InventoryApi inventoryApi;

	private ServiceRequestCommentService serviceRequestCommentService;

	private ContextService<MicroserviceCredentials> contextService;

	@Autowired
	public ServiceRequestUpdateService(EventApi eventApi, AlarmApi alarmApi, InventoryApi inventoryApi,
			ServiceRequestCommentService serviceRequestCommentService,
			ContextService<MicroserviceCredentials> contextService) {
		this.eventApi = eventApi;
		this.alarmApi = alarmApi;
		this.inventoryApi = inventoryApi;
		this.serviceRequestCommentService = serviceRequestCommentService;
		this.contextService = contextService;
	}

	@Async
	public void updateAlarm(ServiceRequest serviceRequest, ServiceRequestDataRef alarmRef,
			ServiceRequestStatusConfig srStatus, MicroserviceCredentials credentials) {
		contextService.runWithinContext(credentials, () -> {
			updateAlarm(serviceRequest, alarmRef, srStatus);
		});
	}

	private void updateAlarm(ServiceRequest serviceRequest, ServiceRequestDataRef alarmRef,
			ServiceRequestStatusConfig srStatus) {
		if ((serviceRequest == null) || (alarmRef == null) || (srStatus == null)) {
			log.error("updateAlarm(serviceRequest: {}, alarmRef: {}, srStatus: {})", serviceRequest, alarmRef,
					srStatus);
			return;
		}
		log.debug("updateAlarm(serviceRequest: {}, alarmRef: {}, srStatus: {})", serviceRequest.getId(),
				alarmRef.getId(), srStatus.getId());

		CumulocityAlarmStatuses alarmStatus = srStatus.getAlarmStatusTransition() != null
				? CumulocityAlarmStatuses.valueOf(srStatus.getAlarmStatusTransition())
				: null;

		if (alarmStatus == null) {
			log.info("No alarm transition defined for service request status: {}", srStatus.getId());
		}

		AlarmRepresentation currentAlarm = null;
		try {
			currentAlarm = alarmApi.getAlarm(GId.asGId(alarmRef.getId()));
		} catch (Exception e) {
			log.error("Error getting alarm {} for service request: {}", alarmRef.getId(), serviceRequest.getId(), e);
			return;
		}

		if (CumulocityAlarmStatuses.CLEARED.name().equals(currentAlarm.getStatus())) {
			log.info("Alarm status is already {}, no update needed!", CumulocityAlarmStatuses.CLEARED.name());
			return;
		}

		AlarmMapper alarmMapper = AlarmMapper.map2(serviceRequest.getId(), alarmRef, alarmStatus);
		if (alarmMapper != null) {
			AlarmRepresentation alarmRepresentation = alarmMapper.getAlarm();
			log.info("update Alarm {}", alarmRepresentation.getId().getValue());
			alarmApi.update(alarmRepresentation);
		}
	}

	@Async
	public void createCommentForStatusChange(String prefix, ServiceRequest serviceRequest,
			MicroserviceCredentials credentials) {
		contextService.runWithinContext(credentials, () -> {
			createCommentForStatusChange(prefix, serviceRequest);
		});
	}

	private void createCommentForStatusChange(String prefix, ServiceRequest serviceRequest) {
		if (serviceRequest == null) {
			log.warn("Couldn't add system comment, service request is null!");
			return;
		}
		String text = prefix + ", Id: " + serviceRequest.getStatus().getId() + ", Name: "
				+ serviceRequest.getStatus().getName();
		createSystemComment(text, serviceRequest);
	}

	private void createSystemComment(String text, ServiceRequest serviceRequest) {
		if (serviceRequest == null) {
			log.warn("Couldn't add system comment, service request is null!");
			return;
		}
		ServiceRequestCommentRqBody comment = new ServiceRequestCommentRqBody();
		comment.setText(text);
		comment.setType(ServiceRequestCommentType.SYSTEM);
		serviceRequestCommentService.createComment(serviceRequest.getSource().getId(), serviceRequest.getId(), comment,
				null);
	}

	@Async
	public void updateAllCommentsToClosed(ServiceRequest serviceRequest, MicroserviceCredentials credentials) {
		contextService.runWithinContext(credentials, () -> {
			updateAllCommentsToClosed(serviceRequest);
		});
	}

	private void updateAllCommentsToClosed(ServiceRequest serviceRequest) {
		List<ServiceRequestComment> commentList = serviceRequestCommentService
				.getCompleteCommentListByServiceRequest(serviceRequest.getId());

		for (ServiceRequestComment comment : commentList) {
			ServiceRequestCommentRqBody commentUpdate = new ServiceRequestCommentRqBody();
			commentUpdate.setIsClosed(Boolean.TRUE);
			serviceRequestCommentService.updateComment(comment.getId(), commentUpdate);
		}
	}

	@Async
	public void updateServiceRequestCounter(ServiceRequest serviceRequest, List<String> excludeList,
			MicroserviceCredentials credentials) {
		contextService.runWithinContext(credentials, () -> {
			updateServiceRequestCounter(serviceRequest, excludeList);
		});
	}

	private void updateServiceRequestCounter(ServiceRequest serviceRequest, List<String> excludeList) {
		log.debug("Update Managed Object");
		ManagedObjectRepresentation source = inventoryApi.get(GId.asGId(serviceRequest.getSource().getId()));
		ManagedObjectMapper moMapper = ManagedObjectMapper.map2(source);
		moMapper.updateServiceRequestPriorityCounter(getAllActiveEventsBySource(source.getId()), excludeList);
		inventoryApi.update(moMapper.getManagedObjectRepresentation());
	}

	private RequestList<ServiceRequest> getAllActiveEventsBySource(GId sourceId) {
		EventFilter filter = new EventFilter();
		filter.byType(ServiceRequestEventMapper.EVENT_TYPE).bySource(sourceId)
				.byFragmentType(ServiceRequestEventMapper.SR_ACTIVE).byFragmentValue(Boolean.TRUE.toString());
		EventCollection eventList = eventApi.getEventsByFilter(filter);

		QueryParam queryParamWithTotalElements = new QueryParam(StatisticsParam.WITH_TOTAL_ELEMENTS, "true");
		//TODO if the number of events is more than 2000, we need to implement pagination!!!
		PagedEventCollectionRepresentation pagedCollection = eventList.get(2000, queryParamWithTotalElements);

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
}
