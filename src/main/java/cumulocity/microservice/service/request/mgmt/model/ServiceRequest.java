package cumulocity.microservice.service.request.mgmt.model;

import java.util.Map;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Service Request object at Cumulocity IoT Platform. This object is the representative / proxy for external system object like ticket, issue, notification, incident etc.. It contains the life cycle information of it, IoT data references (device, alarms, events and measurement) and meta data for the service request like owner, creation time etc.. It has a flexible key & value list \"customProperties\" to add custom properties as well.")
@Validated
public class ServiceRequest {

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Internal ID, set by Cumulocity", example = "1335312474")
	@NotNull
	private String id;

	@Schema(required = true, description = "Service request type")
	@NotNull
	@Valid
	private ServiceRequestType type;

	@Schema(required = true, description = "Service request status")
	@NotNull
	@Valid
	private ServiceRequestStatus status;

	@Schema(description = "Servcie request priority")
	@Valid
	private ServiceRequestPriority priority;

	@Schema(required = true, description = "Service request title / summary", example = "Device XY is out of order")
	@NotNull
	private String title;

	@Schema(description = "Service request detailed description", example = "Device XY is out of order. Please check energy supply.")
	private String description;

	@Schema(required = true, description = "Cumulocity Device (managed object) reference")
	@NotNull
	@Valid
	private ServiceRequestSource source;

	@Schema(description = "Cumulocity Alarm reference, deprecated, use alarmRefList instead of alarmRef!")
	@Valid
	@Deprecated(since = "1.2.0", forRemoval = true)
	private ServiceRequestDataRef alarmRef;

	@Schema(description = "Cumulocity Alarm reference list")
	@Valid
	private Set<ServiceRequestDataRef> alarmRefList;

	@Schema(description = "Cumulocity Event reference")
	@Valid
	private ServiceRequestDataRef eventRef;

	@Schema(description = "Cumulocity Measurement series reference")
	@Valid
	private ServiceRequestDataRef seriesRef;

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Creation date time")
	@NotNull
	@Valid
	private DateTime creationTime;

	@Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Update date time")
	@NotNull
	@Valid
	private DateTime lastUpdated;

	@Schema(required = true, description = "Creator / owner", example = "owner@example.com")
	@NotNull
	private String owner;
	
	@Schema(description = "Service request active flag, shows if the service request is active!", example = "true")
	private Boolean isActive;
	
	@Schema(description = "Service request closed flag, shows if the service request is closed! This fragment is basicaly used for retention rules.")
	private Boolean isClosed;
	
	@Schema(description = "Service request external ID, contains the service request object ID of the external system.", example = "123456789")
	private String externalId;

	@Schema(description = "File attachment of Service Request")
	private ServiceRequestAttachment attachment;
	
	@Schema(description = "Service Order, reference object to any kind of service order. Be aware the the order object must be complete. Partial order objects will set all not initialized properties to null!")
	private ServiceOrder order;

	@Schema(description = "Custom specific properties")
	private Map<String, String> customProperties;
}
