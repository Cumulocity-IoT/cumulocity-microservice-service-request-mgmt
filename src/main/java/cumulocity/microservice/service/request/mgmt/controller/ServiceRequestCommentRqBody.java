package cumulocity.microservice.service.request.mgmt.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestAttachment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Comment object of service request. Service request object can have n comments.")
@Validated
public class ServiceRequestCommentRqBody {

	@Schema(description = "comment text", example = "This is my comment ...")
	@NotNull
	private String text;
	
	@Schema(description = "Service request comment external ID, contains the service request comment object ID of the external system.", example = "123456789")
	private String externalId;

	@Schema(description = "comment type enumeration (USER, SYSTEM)")
	@NotNull
	@Valid
	private ServiceRequestCommentType type;

	@Schema(description = "File attachment of Service Request")
	private ServiceRequestAttachment attachment;

}