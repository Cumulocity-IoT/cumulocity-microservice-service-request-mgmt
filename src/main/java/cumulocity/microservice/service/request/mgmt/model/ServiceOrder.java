package cumulocity.microservice.service.request.mgmt.model;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Service Order object at Cumulocity IoT Platform.")
@Validated
public class ServiceOrder {
    
    @Schema(required = false, description = "External ID of order", example = "1335312474")
	private String id;

    @Schema(required = false, description = "Priority of order", example = "1")
    private String priority;

    @Schema(required = false, description = "Status of order", example = "Pending")
    private String status;
}
