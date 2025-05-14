package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Schema(description = "Defines a custom specific status")
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestStatus {
	@Schema(required = true, description = "Internal id of status", example = "0")
	@NotNull
	@NonNull
	private String id;
	
	@Schema(required = false, description = "Name of status", example = "open")
	private String name;
}
