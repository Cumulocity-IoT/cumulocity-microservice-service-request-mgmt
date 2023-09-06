package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.RequestList;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusService;

class ServiceRequestServiceC8yTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testCreateServiceRequest() {
		InventoryApi inventoryApi = mock(InventoryApi.class);
		
		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
		mo.setId(GId.asGId(123L));
		when(inventoryApi.get(Mockito.any(GId.class))).thenReturn(mo);
		
		EventApi eventApi = mock(EventApi.class);
		EventRepresentation srEvent = new EventRepresentation();
		srEvent.setId(GId.asGId(123L));
		srEvent.setSource(mo);
		when(eventApi.create(Mockito.any(EventRepresentation.class))).thenReturn(srEvent);
		
		EventAttachmentApi eventAttachmentApi = mock(EventAttachmentApi.class);
		AlarmApi alarmApi = mock(AlarmApi.class);
		ServiceRequestStatusService serviceRequestStatusService = mock(ServiceRequestStatusService.class);
		
		//ServiceRequestServiceC8y serviceRequestService = new ServiceRequestServiceC8y(eventApi, eventAttachmentApi, alarmApi, inventoryApi, serviceRequestStatusService);
		ServiceRequestServiceC8y serviceRequestService = mock(ServiceRequestServiceC8y.class, withSettings().useConstructor(eventApi).useConstructor(eventAttachmentApi).useConstructor(alarmApi).useConstructor(inventoryApi).useConstructor(serviceRequestStatusService));
		
		RequestList<ServiceRequest> requestList = new RequestList<>();
		
		when(serviceRequestService.getAllActiveEventsBySource(Mockito.any())).thenReturn(requestList);
		when(serviceRequestService.createServiceRequest(Mockito.any(), Mockito.anyString())).thenCallRealMethod();
		
		ServiceRequestPostRqBody serviceRequestRqBody = new ServiceRequestPostRqBody();
		serviceRequestRqBody.setDescription("Description");
		
		ServiceRequestPriority prio = new ServiceRequestPriority();
		prio.setName("high");
		prio.setOrdinal(1L);
		serviceRequestRqBody.setPriority(prio);
		
		ServiceRequestSource source = new ServiceRequestSource("123");
		serviceRequestRqBody.setSource(source);
		
		ServiceRequestStatus status = new ServiceRequestStatus();
		status.setName("new");
		status.setId("1");
		serviceRequestRqBody.setStatus(status);
		serviceRequestRqBody.setTitle("title");
		serviceRequestRqBody.setType(ServiceRequestType.ALARM);
		
		
		
		serviceRequestService.createServiceRequest(serviceRequestRqBody, "me@test.com");
	}

	@Test
	void testUpdateServiceRequest() {
		fail("Not yet implemented");
	}

	@Test
	void testGetServiceRequestById() {
		fail("Not yet implemented");
	}

	@Test
	void testGetAllServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Test
	void testGetActiveServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCompleteActiveServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteServiceRequest() {
		fail("Not yet implemented");
	}

	@Test
	void testUploadAttachment() {
		fail("Not yet implemented");
	}

	@Test
	void testDownloadAttachment() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateServiceRequestStatus() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateServiceRequestActive() {
		fail("Not yet implemented");
	}

}
