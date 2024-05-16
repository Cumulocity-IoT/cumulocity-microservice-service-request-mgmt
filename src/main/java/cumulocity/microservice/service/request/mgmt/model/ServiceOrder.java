package cumulocity.microservice.service.request.mgmt.model;

import org.springframework.validation.annotation.Validated;
import org.svenson.JSONProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

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

    @JSONProperty(ignoreIfNull = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONProperty(ignoreIfNull = true)
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @JSONProperty(ignoreIfNull = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}