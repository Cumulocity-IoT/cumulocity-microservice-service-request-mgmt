package cumulocity.microservice.service.request.mgmt.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cumulocity.rest.representation.event.EventRepresentation;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;

class ServiceRequestEventMapperTest {	
	
	private ServiceRequestStatus status = new ServiceRequestStatus("1", "open");
	private ServiceRequestPriority priority = new ServiceRequestPriority("high", 1L);
	private ServiceRequestDataRef alarmRef = new ServiceRequestDataRef("https://your-tenant.cumulocity.com/alarm/alarms/18135043");
	private ServiceRequestDataRef deviceRef = new ServiceRequestDataRef("https://your-tenant.cumulocity.com/inventory/managedObjects/18543");
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void mapServiceRequestRqBody2EventRepresentationTest() {
		ServiceRequestPostRqBody serviceRequest = new ServiceRequestPostRqBody();
		serviceRequest.setAlarmRef(alarmRef);
		serviceRequest.setDescription("Hello my friend, i need some help. Could you please ...");
		deviceRef.setId("18543");
		serviceRequest.setDeviceRef(deviceRef);
		serviceRequest.setEventRef(null);
		serviceRequest.setOwner("owner@example.com");
		serviceRequest.setPriority(priority);
		serviceRequest.setSeriesRef(null);		
		serviceRequest.setStatus(status);
		serviceRequest.setTitle("My first service request");
		serviceRequest.setType(ServiceRequestType.ALARM);
		
		
		ServiceRequestEventMapper srEventMapper = ServiceRequestEventMapper.map2(serviceRequest);
		assertNotNull(srEventMapper);
		assertEquals(alarmRef, srEventMapper.getAlarmRef());
		assertEquals("Hello my friend, i need some help. Could you please ...", srEventMapper.getDescription());
		assertEquals(deviceRef,srEventMapper.getDeviceRef());
		assertNull(srEventMapper.getEventRef());
		assertEquals("owner@example.com", srEventMapper.getOwner());
		assertEquals(priority, srEventMapper.getPriority());
		assertNull(srEventMapper.getSeriesRef());
		assertEquals(status, srEventMapper.getStatus());
		assertEquals("My first service request", srEventMapper.getTitle());
		assertEquals(ServiceRequestType.ALARM, srEventMapper.getServiceRequestType());
	}
	
}
