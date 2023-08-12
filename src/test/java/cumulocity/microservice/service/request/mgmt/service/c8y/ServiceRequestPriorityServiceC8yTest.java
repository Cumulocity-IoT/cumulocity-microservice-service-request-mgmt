package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;

class ServiceRequestPriorityServiceC8yTest {
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void test() {
		InventoryApi inventoryApi = mock(InventoryApi.class);
		when(inventoryApi.get(null)).thenReturn(null);
		
		when(inventoryApi.create(null)).thenReturn(null);
	}
	
	@Test
	void getPriorityTest() {
		ServiceRequestPriorityServiceC8y priorityService = mock(ServiceRequestPriorityServiceC8y.class);
		ManagedObjectRepresentation priorityMo = createMo(1L, 2L, 3L);
		
		when(priorityService.getManagedObjectRepresentation()).thenReturn(priorityMo);
		when(priorityService.getPriority(Mockito.anyLong())).thenCallRealMethod();
		
		ServiceRequestPriority priority = priorityService.getPriority(2L);
		assertNotNull(priority);
		assertEquals(2L, priority.getOrdinal());
		assertEquals("Prio2", priority.getName());
		
		ServiceRequestPriority priorityEmpty = priorityService.getPriority(4L);
		assertNull(priorityEmpty);
	}
	
	@Test
	void getPriorityListTest() {
		ServiceRequestPriorityServiceC8y priorityService = mock(ServiceRequestPriorityServiceC8y.class);
		ManagedObjectRepresentation priorityMo = createMo(1L, 2L, 3L);
		
		when(priorityService.getManagedObjectRepresentation()).thenReturn(priorityMo);
		when(priorityService.getPriorityList()).thenCallRealMethod();
		
		
		List<ServiceRequestPriority> priorityList = priorityService.getPriorityList();
		assertNotNull(priorityList);
		assertEquals(3, priorityList.size());
	}
	
	@Test
	void createOrUpdatePriorityListTest() {
		InventoryApi inventoryApi = mock(InventoryApi.class);
		ManagedObjectRepresentation updatedPriorityMo = createMo(5L, 6L);
		when(inventoryApi.update(Mockito.any())).thenReturn(updatedPriorityMo);	
		
		ServiceRequestPriorityServiceC8y priorityService = mock(ServiceRequestPriorityServiceC8y.class, withSettings().useConstructor(inventoryApi));
		ManagedObjectRepresentation priorityMo = createMo(1L, 2L, 3L);
		
		when(priorityService.getManagedObjectRepresentation()).thenReturn(priorityMo);
		when(priorityService.createOrUpdatePriorityList(Mockito.anyList())).thenCallRealMethod();
		
		
		List<ServiceRequestPriority> newPriorityList = new ArrayList<>();
		newPriorityList.add(new ServiceRequestPriority("Prio5", 5L));
		newPriorityList.add(new ServiceRequestPriority("Prio6", 6L));
		
		
		List<ServiceRequestPriority> createOrUpdatePriorityList = priorityService.createOrUpdatePriorityList(newPriorityList);
		assertNotNull(createOrUpdatePriorityList);
		assertEquals(2, createOrUpdatePriorityList.size());
	}
	
	@Test
	void deletePriorityTest() {
		InventoryApi inventoryApi = mock(InventoryApi.class);
		ManagedObjectRepresentation updatedPriorityMo = createMo(1L, 2L);
		when(inventoryApi.update(Mockito.any())).thenReturn(updatedPriorityMo);			

		ServiceRequestPriorityServiceC8y priorityService = mock(ServiceRequestPriorityServiceC8y.class, withSettings().useConstructor(inventoryApi));
		ManagedObjectRepresentation priorityMo = createMo(1L, 2L, 3L);
		
		when(priorityService.getManagedObjectRepresentation()).thenReturn(priorityMo);
		doCallRealMethod().when(priorityService).deletePriority(Mockito.anyLong());
		
		priorityService.deletePriority(3L);
		priorityService.deletePriority(5L);
	}

	private static ManagedObjectRepresentation createMo(Long... priorities) {
		List<HashMap<String, Object>> hashMapList = new ArrayList<>();
		
		for(Long priority: priorities) {
			HashMap<String, Object> prio = new HashMap<>();
			prio.put(ServiceRequestPriorityObjectMapper.SR_PRIORITY_ORDINAL, priority);
			prio.put(ServiceRequestPriorityObjectMapper.SR_PRIORITY_NAME, "Prio"+priority);
			hashMapList.add(prio);
		}
		
		ManagedObjectRepresentation priorityMo = new ManagedObjectRepresentation();
		priorityMo.set(hashMapList, ServiceRequestPriorityObjectMapper.SR_PRIORITY_LIST);
		priorityMo.setId(GId.asGId(123L));
		
		return priorityMo;
	}
}
