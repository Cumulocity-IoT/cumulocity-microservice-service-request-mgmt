package cumulocity.microservice.service.request.mgmt.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Schema(description = "Reference to data at IoT Platform. It can contain an URI with points to a specific object using id, it could also contain a URI with query parameters to reference a list of objects.")
@RequiredArgsConstructor
@NoArgsConstructor
@Validated
public class ServiceRequestSource {
	@Schema(description = "Internal id of IoT data object", example = "18135043")
	@NotNull
	@NonNull
	private String id;

	@Schema(required = true, description = "uri of IoT data", example = "https://your-tenant.cumulocity.com/alarm/alarms/18135043")
	@NotNull
	@NonNull
	private String self;

	@JsonIgnore
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
