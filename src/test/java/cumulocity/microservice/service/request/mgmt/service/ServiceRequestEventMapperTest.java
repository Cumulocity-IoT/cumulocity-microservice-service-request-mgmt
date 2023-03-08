package cumulocity.microservice.service.request.mgmt.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cumulocity.rest.representation.event.EventRepresentation;

import cumulocity.microservice.service.request.mgmt.controller.ServiceRequestPostRqBody;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestEventMapper;

class ServiceRequestEventMapperTest {	
	
	private ServiceRequestStatus status = new ServiceRequestStatus("1", "open");
	private ServiceRequestPriority priority = new ServiceRequestPriority("high", 1L);
	private ServiceRequestDataRef alarmRef = new ServiceRequestDataRef("https://your-tenant.cumulocity.com/alarm/alarms/18135043");
	private ServiceRequestSource source = new ServiceRequestSource("18543");
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void mapServiceRequestRqBody2EventRepresentationTest() {
		ServiceRequestPostRqBody serviceRequest = new ServiceRequestPostRqBody();
		serviceRequest.setAlarmRef(alarmRef);
		serviceRequest.setDescription("Hello my friend, i need some help. Could you please ...");
		serviceRequest.setSource(source);
		serviceRequest.setEventRef(null);
		serviceRequest.setPriority(priority);
		serviceRequest.setSeriesRef(null);		
		serviceRequest.setStatus(status);
		serviceRequest.setTitle("My first service request");
		serviceRequest.setType(ServiceRequestType.ALARM);
		
		
		ServiceRequestEventMapper srEventMapper = ServiceRequestEventMapper.map2(serviceRequest);
		assertNotNull(srEventMapper);
		assertEquals(alarmRef, srEventMapper.getAlarmRef());
		assertEquals("Hello my friend, i need some help. Could you please ...", srEventMapper.getDescription());
		assertEquals(source,srEventMapper.getSource());
		assertNull(srEventMapper.getEventRef());
		assertEquals("owner@example.com", srEventMapper.getOwner());
		assertEquals(priority, srEventMapper.getPriority());
		assertNull(srEventMapper.getSeriesRef());
		assertEquals(status, srEventMapper.getStatus());
		assertEquals("My first service request", srEventMapper.getTitle());
		assertEquals(ServiceRequestType.ALARM, srEventMapper.getServiceRequestType());
	}
	
}
