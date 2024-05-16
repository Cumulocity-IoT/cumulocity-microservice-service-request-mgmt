package cumulocity.microservice.service.request.mgmt.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cumulocity.microservice.service.request.mgmt.model.ServiceOrder;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestDataRef;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestPriority;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestStatus;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Service Request object at Cumulocity IoT Platform. This object is the representative / proxy for external system object like ticket, issue, notification, incident etc.. It contains the life cycle information of it, IoT data references (device, alarms, events and measurement) and meta data for the service request like owner, creation time etc.. It has a flexible key & value list \"customProperties\" to add custom properties as well.")
public class ServiceRequestPostRqBody {

	@Schema(required = true, description = "Service request type")
	@NotNull
	@NonNull
	@Valid
	private ServiceRequestType type;

	@Schema(required = true, description = "Service request status")
	@NotNull
	@NonNull
	@Valid
	private ServiceRequestStatus status;

	@Schema(description = "Servcie request priority")
	@Valid
	private ServiceRequestPriority priority;

	@Schema(required = true, description = "Service request title / summary")
	@NotNull
	@NonNull
	private String title;

	@Schema(description = "Service request detailed description")
	private String description;

	@Schema(required = true, description = "Cumulocity Device (managed object) reference")
	@NotNull
	@NonNull
	@Valid
	private ServiceRequestSource source;

	@Schema(description = "Cumulocity Alarm reference")
	@Valid
	private ServiceRequestDataRef alarmRef;

	@Schema(description = "Cumulocity Event reference")
	@Valid
	private ServiceRequestDataRef eventRef;

	@Schema(description = "Cumulocity Measurement series reference")
	@Valid
	private ServiceRequestDataRef seriesRef;

	@Schema(description = "Service Order")
	@Valid
	private ServiceOrder order;

	@Schema(description = "Custom specific properties")
	@Valid
	private Map<String, String> customProperties;
}
