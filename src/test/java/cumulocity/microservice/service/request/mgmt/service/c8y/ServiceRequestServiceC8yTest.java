package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusConfigService;

class ServiceRequestServiceC8yTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Disabled("Disabled until issue on github actions is fixed, locally it is working!")
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
		ServiceRequestStatusConfigService serviceRequestStatusService = mock(ServiceRequestStatusConfigService.class);
		
		ServiceRequestServiceC8y serviceRequestService = mock(ServiceRequestServiceC8y.class, withSettings().useConstructor(eventApi, eventAttachmentApi, alarmApi, inventoryApi, serviceRequestStatusService));
		
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
    public void testGetPages() {
        ServiceRequestServiceC8y service = new ServiceRequestServiceC8y(null, null, null, null, null, null);
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        List<List<Integer>> pages = service.getPages(list, 5);

        assertEquals(4, pages.size());

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), pages.get(0));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), pages.get(1));
        assertEquals(Arrays.asList(6, 7, 8, 9, 10), pages.get(2));
		assertEquals(Arrays.asList(11, 12), pages.get(3));
    }

	@Disabled("Disabled until implementation is done!")
	@Test
	void testUpdateServiceRequest() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testGetServiceRequestById() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testGetAllServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testGetActiveServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testGetCompleteActiveServiceRequestByFilter() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testDeleteServiceRequest() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testUploadAttachment() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testDownloadAttachment() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testUpdateServiceRequestStatus() {
		fail("Not yet implemented");
	}

	@Disabled("Disabled until implementation is done!")
	@Test
	void testUpdateServiceRequestActive() {
		fail("Not yet implemented");
	}

}
