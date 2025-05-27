package cumulocity.microservice.service.request.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ServiceRequestType represents the type of a service request in the Cumulocity IoT Platform.
 * It can be either an ALARM or a NOTE.
 */
public enum ServiceRequestType {
	ALARM("alarm"), NOTE("note");

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
