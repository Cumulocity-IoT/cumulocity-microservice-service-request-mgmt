package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;

class ServiceRequestComparatorTest {

	static List<ServiceRequest> serviceRequestList = new ArrayList<>();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		ServiceRequest sr1 = createServiceRequest(10, "high", 1L, "open", "1");
		sr1.setId("1");
		ServiceRequest sr2 = createServiceRequest(11, "high", 1L, "closed", "2");
		sr2.setId("2");
		ServiceRequest sr3 = createServiceRequest(12, "medium", 2L, "open", "1");
		sr3.setId("3");
		ServiceRequest sr4 = createServiceRequest(12, "low", 3L, "open", "1");
		sr4.setId("4");
		ServiceRequest sr5 = createServiceRequest(9, "high", 1L, "open", "1");
		sr5.setId("5");
		ServiceRequest sr6 = createServiceRequest(9, "medium", 2L, "open", "1");
		sr6.setId("6");
		
		serviceRequestList.add(sr6);
		serviceRequestList.add(sr5);
		serviceRequestList.add(sr4);
		serviceRequestList.add(sr3);
		serviceRequestList.add(sr2);
		serviceRequestList.add(sr1);
		
	}

	private static ServiceRequest createServiceRequest(int hourOfDay, String priorityName, Long priorityOrdinal, String statusName, String statusId) {
		ServiceRequest sr = new ServiceRequest();
		DateTime dateTime = new DateTime(2023, 8, 1, hourOfDay, 0);
		sr.setCreationTime(dateTime);
		
		ServiceRequestPriority priority = new ServiceRequestPriority();
		priority.setName(priorityName);
		priority.setOrdinal(priorityOrdinal);
		sr.setPriority(priority);
		
		ServiceRequestStatus status = new ServiceRequestStatus();
		status.setName(statusName);
		status.setId(statusId);
		sr.setStatus(status);
		
		return sr;
	}
	
	@Test
	void testStatusPriorityTimestamp() {
		String orderBy[] = new String[] {
				"status", "priority", "timestamp"
		};
		ServiceRequestComparator comp = new ServiceRequestComparator(orderBy);
		
		serviceRequestList.sort(comp);
		serviceRequestList.forEach((sr)->System.out.println(sr.getId()));
		String expecte[] = new String[] {
				"5", "1", "6", "3", "4", "2"
		};
		
		for (int i = 0; i < expecte.length; i++) {
			assertEquals(expecte[i], serviceRequestList.get(i).getId());
		}
		
	}
	
	@Test
	void testTimestampPriorityStatus() {
		String orderBy[] = new String[] {
				"timestamp", "priority", "status"
		};
		ServiceRequestComparator comp = new ServiceRequestComparator(orderBy);
		
		serviceRequestList.sort(comp);
		serviceRequestList.forEach((sr)->System.out.println(sr.getId()));
		String expecte[] = new String[] {
				"5", "6", "1", "2", "3", "4"
		};
		
		for (int i = 0; i < expecte.length; i++) {
			assertEquals(expecte[i], serviceRequestList.get(i).getId());
		}
		
	}

}
