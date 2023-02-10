package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Service request reference object, contains internal and external ID")
@Validated
public class ServiceRequestRef {
	@Schema(required = true, description = "Internal ID at Cumulocity IoT", example = "1335312474")
	@NotNull
	private String id;

	@Schema(required = true, description = "External ID", example = "IDD_kjdf66r6123zgsdf6238")
	@NotNull
	private String externalId;
}
