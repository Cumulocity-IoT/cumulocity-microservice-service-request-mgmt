package cumulocity.microservice.service.request.mgmt.model;

import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Service Request object at Cumulocity IoT Platform. This object is the representative / proxy for external system object like ticket, issue, notification, incident etc.. It contains the life cycle information of it, IoT data references (device, alarms, events and measurement) and meta data for the service request like owner, creation time etc.. It has a flexible key & value list \"customProperties\" to add custom properties as well.")
@Validated
public class ServiceOrder {
    
    @Schema(required = true, description = "External ID of order", example = "1335312474")
	@NotNull
	private String id;

    @Schema(required = false, description = "Priority of order", example = "1")
    private String priority;

    @Schema(required = false, description = "Status of order", example = "Pending")
    private String status;
}
