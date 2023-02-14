package cumulocity.microservice.service.request.mgmt.model;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "creation time", example = "2022-08-24T14:15:22Z")
	@NotNull
	@Valid
	private ZonedDateTime creationTime;

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Update date time", example = "2023-02-08T12:00:00:00Z")
	@NotNull
	@Valid
	private ZonedDateTime lastUpdated;

	@Schema(description = "comment text", example = "This is my comment ...")
	private String text;

	@Schema(description = "comment type enumeration (USER, SYSTEM)")
	@NotNull
	@Valid
	private ServiceRequestCommentType type;
	
	@Schema(description = "File attachment of Service Request")
	private ServiceRequestAttachment attachment;

}