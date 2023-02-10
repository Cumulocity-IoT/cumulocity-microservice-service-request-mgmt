package cumulocity.microservice.service.request.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ServiceRequestCommentType {
	USER("user"), SYSTEM("system");

	private String value;

	ServiceRequestCommentType(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static ServiceRequestCommentType fromValue(String text) {
		for (ServiceRequestCommentType b : ServiceRequestCommentType.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
