package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.service.ServiceRequestStatusService;

class ServiceRequestServiceC8yTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testCreateServiceRequest() {
		InventoryApi inventoryApi = mock(InventoryApi.class);
		EventApi eventApi = mock(EventApi.class);
		EventAttachmentApi eventAttachmentApi = mock(EventAttachmentApi.class);
		AlarmApi alarmApi = mock(AlarmApi.class);
		ServiceRequestStatusService serviceRequestStatusService = mock(ServiceRequestStatusService.class);
		
		ServiceRequestServiceC8y serviceRequestService = new ServiceRequestServiceC8y(eventApi, eventAttachmentApi, alarmApi, inventoryApi, serviceRequestStatusService);
		
		
		ServiceRequestPostRqBody serviceRequestRqBody = new ServiceRequestPostRqBody();
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
