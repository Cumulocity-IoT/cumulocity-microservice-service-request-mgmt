package cumulocity.microservice.service.request.mgmt.service.c8y;

import lombok.Data;

@Data
public class BinaryInfo {
	/**
	 * Name of the binary object.
	 */
	private String name;

	/**
	 * Media type of the file.
	 */
	private String type;
}
