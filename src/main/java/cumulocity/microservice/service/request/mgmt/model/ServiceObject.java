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
@RequiredArgsConstructor
@NoArgsConstructor
@Schema(description = "Service object at Cumulocity IoT Platform. This object is a one to one relationship to the object (device) at Cumulocity IoT and represents the object / location of the service.")
@Validated
public class ServiceObject {
	@Schema(description = "Internal id of service object (device)", example = "18135043")
	@NotNull
	@NonNull
	private Long id;

	@Schema(required = true, description = "External id of service object (device)", example = "34kj098fhdgn")
	@NotNull
	@NonNull
	private String externalId;

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