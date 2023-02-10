package cumulocity.microservice.service.request.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ServiceRequestType {
	ALARM("alarm");

	private String value;

	ServiceRequestType(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static ServiceRequestType fromValue(String text) {
		for (ServiceRequestType b : ServiceRequestType.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
