package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Schema(description = "Reference to data at IoT Platform. It can contain an URI with points to a specific object using id, it could also contain a URI with query parameters to reference a list of objects.")
@RequiredArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestDataRef {
	@Schema(description = "Internal id of IoT data object", example = "18135043")
	private String id;

	@Schema(required = false, description = "uri of IoT data", example = "https://your-tenant.cumulocity.com/alarm/alarms/18135043")
	@NotNull
	@NonNull
	private String uri;
}
