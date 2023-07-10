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
public class ServiceRequestStatus {
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
	
	@Schema(required = false, description = "Closed Service Request when this status of Service Request is set. (Transition)", example = "true")	
	private Boolean isClosedTransition;
}
