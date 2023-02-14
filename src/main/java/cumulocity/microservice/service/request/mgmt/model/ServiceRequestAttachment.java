package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "File attachment (binary)")
@Validated
public class ServiceRequestAttachment {
	
	@Schema(required = true, description = "Length of file", example = "1024")
	@NotNull
	@NonNull
	private Long length;
	
	@Schema(required = true, description = "Filename", example = "device.jpg")
	@NotNull
	@NonNull
	private String name;
	
	@Schema(required = true, description = "Content type", example = "application/octet-stream")
	@NotNull
	@NonNull
	private String type;
}
