package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Comment object of service request. Service request object can have n comments.")
@Validated
public class ServiceRequestComment {

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Internal id of comment", example = "1234")
	@NotNull
	private String id;

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "creator / owner ", example = "owner@example.com")
	@NotNull
	private String owner;

	@Schema(required = true, description = "Cumulocity Device (managed object) reference")
	@NotNull
	@Valid
	private ServiceRequestSource source;
	
	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "creation time", example = "2022-08-24T14:15:22Z")
	@NotNull
	@Valid
	private DateTime creationTime;

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Update date time", example = "2023-02-08T12:00:00:00Z")
	@NotNull
	@Valid
	private DateTime lastUpdated;

	@Schema(description = "comment text", example = "This is my comment ...")
	private String text;

	@Schema(description = "comment type enumeration (USER, SYSTEM)")
	@NotNull
	@Valid
	private ServiceRequestCommentType type;
	
	@Schema(description = "Internal id of service request")
	@NotNull
	private String serviceRequestId;
	
	@Schema(description = "Service request comment external ID, contains the service request comment object ID of the external system.", example = "123456789")
	private String externalId;
	
	@Schema(description = "File attachment of Service Request")
	private ServiceRequestAttachment attachment;

}