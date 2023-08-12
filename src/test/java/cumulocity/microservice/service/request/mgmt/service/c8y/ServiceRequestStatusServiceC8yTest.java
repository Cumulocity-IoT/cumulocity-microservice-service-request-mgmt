package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;

class ServiceRequestStatusServiceC8yTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testCreateOrUpdateStatusList() {
		fail("Not yet implemented");
	}

	@Test
	void testGetStatusList() {
		ServiceRequestStatusServiceC8y statusService = mock(ServiceRequestStatusServiceC8y.class);
		ManagedObjectRepresentation priorityMo = createMo("1", "2", "3");
		
		when(statusService.getManagedObjectRepresentation()).thenReturn(priorityMo);
		when(statusService.getStatusList()).thenCallRealMethod();
	}

	@Test
	void testGetStatus() {
		ServiceRequestStatusServiceC8y statusService = mock(ServiceRequestStatusServiceC8y.class);
		ManagedObjectRepresentation statusListMo = createMo("1", "2", "3");
		
		when(statusService.getManagedObjectRepresentation()).thenReturn(statusListMo);
		when(statusService.getStatus(Mockito.anyString())).thenCallRealMethod();
		
		Optional<ServiceRequestStatus> status = statusService.getStatus("2");
		assertNotNull(status);
		assertEquals("2", status.get().getId());
		assertEquals("Status2", status.get().getName());
		
		Optional<ServiceRequestStatus> statusEmpty = statusService.getStatus("4");
		assertEquals(true, statusEmpty.isEmpty());
	}

	@Test
	void testDeleteStatus() {
		fail("Not yet implemented");
	}
	
	private static ManagedObjectRepresentation createMo(String... statusList) {
		List<HashMap<String, Object>> hashMapList = new ArrayList<>();
		
		for(String status: statusList) {
			HashMap<String, Object> statusObj = new HashMap<>();
			statusObj.put(ServiceRequestStatusObjectMapper.SR_STATUS_ID, status);
			statusObj.put(ServiceRequestStatusObjectMapper.SR_STATUS_NAME, "Status"+status);
			statusObj.put(ServiceRequestStatusObjectMapper.SR_ALARM_STATUS_TRANSITION, "CLEARED");
			statusObj.put(ServiceRequestStatusObjectMapper.SR_IS_CLOSED_TRANSITION, Boolean.TRUE);
			hashMapList.add(statusObj);
		}
		
		ManagedObjectRepresentation priorityMo = new ManagedObjectRepresentation();
		priorityMo.set(hashMapList, ServiceRequestStatusObjectMapper.SR_STATUS_LIST);
		priorityMo.setId(GId.asGId(123L));
		
		return priorityMo;
	}

}
