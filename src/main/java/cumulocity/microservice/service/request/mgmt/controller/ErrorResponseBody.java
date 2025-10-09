package cumulocity.microservice.service.request.mgmt.controller;

import cumulocity.microservice.service.request.mgmt.model.ContextDataApply.ContextDataApplyError;
import cumulocity.microservice.service.request.mgmt.service.c8y.ServiceRequestServiceC8y.ServiceRequestValidationResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Response body for error. This can contain additional error message for the client.")
public class ErrorResponseBody {

    public ErrorResponseBody(ServiceRequestValidationResult validationResult) {
        this.message = validationResult.getMessage();
        this.errorCode = validationResult.name();
    }

    public ErrorResponseBody(ContextDataApplyError contextDataApplyError) {
        this.message = contextDataApplyError.getMessage();
        this.errorCode = contextDataApplyError.name();
    }

    private final String message;

    private final String errorCode;
}
