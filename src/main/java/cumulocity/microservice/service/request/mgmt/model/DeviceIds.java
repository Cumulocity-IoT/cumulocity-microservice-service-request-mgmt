package cumulocity.microservice.service.request.mgmt.model;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Schema(description = "Service Order object at Cumulocity IoT Platform.")
@Validated
public class DeviceIds {
    
    @Schema(description = "Cumulocity managed object IDs")
	@Valid
    private Set<String> deviceIds;

    public Set<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(Set<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}