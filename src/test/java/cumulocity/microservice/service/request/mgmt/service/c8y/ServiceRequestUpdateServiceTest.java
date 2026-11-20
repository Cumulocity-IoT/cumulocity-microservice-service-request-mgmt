package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.PageStatisticsRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.QueryParam;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.event.PagedEventCollectionRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatusConfig;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusConfigService;

class ServiceRequestUpdateServiceTest {

	private static final String DEVICE_ID = "848961986";

	private EventApi eventApi;
	private InventoryApi inventoryApi;
	private MicroserviceSettingsService microserviceSettingsService;
	private ServiceRequestStatusConfigService statusConfigService;
	private PagedEventCollectionRepresentation pagedEvents;
	private ServiceRequestUpdateService service;

	@BeforeEach
	void setUp() {
		eventApi = mock(EventApi.class);
		inventoryApi = mock(InventoryApi.class);
		microserviceSettingsService = mock(MicroserviceSettingsService.class);
		statusConfigService = mock(ServiceRequestStatusConfigService.class);

		EventCollection eventCollection = mock(EventCollection.class);
		pagedEvents = mock(PagedEventCollectionRepresentation.class);
		when(eventApi.getEventsByFilter(any(EventFilter.class))).thenReturn(eventCollection);
		when(eventCollection.get(anyInt(), any(QueryParam.class))).thenReturn(pagedEvents);
		when(pagedEvents.getPageStatistics()).thenReturn(new PageStatisticsRepresentation());

		// device currently shows a stale counter
		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		device.setId(GId.asGId(DEVICE_ID));
		Map<String, Long> staleCounter = new HashMap<>();
		staleCounter.put("high", 1L);
		device.set(staleCounter, ManagedObjectMapper.SR_ACTIVE_STATUS);
		when(inventoryApi.get(any(GId.class))).thenReturn(device);

		// tenant setting: only alarm service requests are counted
		when(microserviceSettingsService.get("activeStatusIncludeTypes")).thenReturn("[\"alarm\"]");

		// status configuration as used at Enercon: only "10" (Open) is counted
		when(statusConfigService.getStatusList()).thenReturn(Arrays.asList(
				status("0", "undefined / error", true),
				status("10", "Open", false),
				status("20", "Closed", true),
				status("40", "Order scheduled", true),
				status("60", "Order completed", true),
				status("70", "Order cancelled", true)));

		service = new ServiceRequestUpdateService(eventApi, null, null, inventoryApi, null, null, null,
				microserviceSettingsService, statusConfigService);
	}

	/**
	 * Regression test: an update without status and priority (e.g. the SAP adapter only setting the external ID of a
	 * downtime service request) must not count service requests in a status that is excluded from the counter.
	 */
	@Test
	void updateServiceRequestCounter_ignoresExcludedStatusesForUpdateWithoutStatus() {
		when(pagedEvents.getEvents()).thenReturn(List.of(
				srEvent("1", "downtime", "10", "Open", "high"),
				srEvent("2", "downtime", "10", "Open", "high"),
				srEvent("3", "alarm", "60", "Order completed", "high")));

		service.updateServiceRequestCounter(serviceRequestOnDevice());

		Map<String, Long> counter = capturedCounter();
		assertTrue(counter.isEmpty(), "expected no active service requests to be counted, but got " + counter);
	}

	@Test
	void updateServiceRequestCounter_countsNotExcludedStatusesPerPriority() {
		when(pagedEvents.getEvents()).thenReturn(List.of(
				srEvent("1", "alarm", "10", "Open", "high"),
				srEvent("2", "alarm", "10", "Open", "medium"),
				srEvent("3", "alarm", "10", "Open", "medium"),
				srEvent("4", "alarm", "70", "Order cancelled", "high"),
				srEvent("5", "downtime", "10", "Open", "high")));

		service.updateServiceRequestCounter(serviceRequestOnDevice());

		Map<String, Long> counter = capturedCounter();
		assertEquals(2, counter.size());
		assertEquals(1L, counter.get("high"));
		assertEquals(2L, counter.get("medium"));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Long> capturedCounter() {
		ArgumentCaptor<ManagedObjectRepresentation> captor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
		verify(inventoryApi).update(captor.capture());
		return (Map<String, Long>) captor.getValue().get(ManagedObjectMapper.SR_ACTIVE_STATUS);
	}

	private static ServiceRequest serviceRequestOnDevice() {
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setSource(new ServiceRequestSource(DEVICE_ID));
		return serviceRequest;
	}

	private static ServiceRequestStatusConfig status(String id, String name, boolean excludeForCounter) {
		ServiceRequestStatusConfig config = new ServiceRequestStatusConfig();
		config.setId(id);
		config.setName(name);
		config.setIsExcludeForCounter(excludeForCounter);
		return config;
	}

	private static EventRepresentation srEvent(String id, String type, String statusId, String statusName, String priority) {
		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(GId.asGId(DEVICE_ID));

		EventRepresentation event = new EventRepresentation();
		event.setId(GId.asGId(id));
		event.setType(ServiceRequestEventMapper.EVENT_TYPE);
		event.setSource(source);
		event.set("true", ServiceRequestEventMapper.SR_ACTIVE);
		event.set(type, ServiceRequestEventMapper.SR_TYPE);

		HashMap<String, String> status = new HashMap<>();
		status.put("id", statusId);
		status.put("name", statusName);
		event.set(status, ServiceRequestEventMapper.SR_STATUS);

		HashMap<String, Object> prio = new HashMap<>();
		prio.put("name", priority);
		event.set(prio, ServiceRequestEventMapper.SR_PRIORITY);
		return event;
	}
}
