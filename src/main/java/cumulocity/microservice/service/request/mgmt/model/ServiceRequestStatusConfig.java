package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Schema(description = "Defines a custom specific status")
@RequiredArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestStatusConfig {
	@Schema(required = true, description = "Internal id of status", example = "0")
	@NotNull
	@NonNull
	private String id;
	
	@Schema(required = true, description = "Name of status", example = "open")
	@NotNull
	@NonNull
	private String name;
	
	@Schema(required = false, description = "Set Alarm status when this status of Service Request status is set. (Transition)", example = "ACKNOWLEDGED")
	private String alarmStatusTransition;
	
	@Schema(required = false, description = "Closes Service Request when this status of Service Request is set.", example = "true")	
	private Boolean isClosedTransition;
	
	@Schema(required = false, description = "Deactivates Service Request when this status of Service Request is set. The service request disappears of the list.", example = "true")
	private Boolean isDeactivateTransition;
	
	@Schema(required = false, description = "All active (not closed) Service Requests are counted on device manage object. However, with with parameter the status can be excluded and will not be counted!", example = "false")	
	private Boolean excludeForCounter;
	
	@Schema(required = false, description = "Icon name of the status which should be shown at the UI", example = "warning")		
	private String icon;
	
}
