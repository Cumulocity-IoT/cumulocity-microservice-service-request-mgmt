package cumulocity.microservice.service.request.mgmt.service.c8y;

import lombok.Data;

@Data
public class EventBinary {
	/**
	 * Name of the attachment. If it is not provided in the request, it will be set as the event ID.
	 */
	private String name;

	/**
	 * A URL linking to this resource.
	 */
	private String self;

	/**
	 * Unique identifier of the event.
	 */
	private String source;

	/**
	 * Media type of the attachment.
	 */
	private String type;
}
