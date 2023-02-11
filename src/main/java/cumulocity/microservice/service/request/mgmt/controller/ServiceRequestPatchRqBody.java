package cumulocity.microservice.service.request.mgmt.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Service Request object at Cumulocity IoT Platform. This object is the representative / proxy for external system object like ticket, issue, notification, incident etc.. It contains the life cycle information of it, IoT data references (device, alarms, events and measurement) and meta data for the service request like owner, creation time etc.. It has a flexible key & value list \"customProperties\" to add custom properties as well.")
@Validated
public class ServiceRequestPatchRqBody {

	@Schema(required = true, description = "Service request status")
	@NotNull
	@Valid
	private ServiceRequestStatus status;

	@Schema(description = "Servcie request priority")
	@Valid
	private ServiceRequestPriority priority;

	@Schema(required = true, description = "Service request title / summary")
	@NotNull
	private String title;

	@Schema(description = "Service request detailed description")
	private String description;

	@Schema(description = "Service request active flag, shows if the service request is active!", example = "true")
	private Boolean isActive;

	@Schema(description = "Custom specific properties")
	private Map<String, String> customProperties;
}
