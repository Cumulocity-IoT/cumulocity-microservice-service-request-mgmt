package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequest;

public class ServiceRequestComparator implements Comparator<ServiceRequest> {

	private final String[] orderBy;
	
	public ServiceRequestComparator(String[] orderBy) {
		super();
		this.orderBy = orderBy;
	}

	@Override
	public int compare(ServiceRequest o1, ServiceRequest o2) {
		ComparisonChain comparisonChain = ComparisonChain.start();
		for(String orderByClass: orderBy) {
			if("status".equalsIgnoreCase(orderByClass)) {
				comparisonChain = comparisonChain.compare(o1.getStatus().getId(), o2.getStatus().getId());
			}else if("priority".equalsIgnoreCase(orderByClass)) {
				comparisonChain = comparisonChain.compare(o1.getPriority().getOrdinal(), o2.getPriority().getOrdinal());
			}else if("timestamp".equalsIgnoreCase(orderByClass)) {
				comparisonChain = comparisonChain.compare(o1.getCreationTime(), o2.getCreationTime());
			}
		}
		return comparisonChain.result();
	}

}
