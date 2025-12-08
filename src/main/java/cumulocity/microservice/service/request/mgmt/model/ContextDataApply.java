package cumulocity.microservice.service.request.mgmt.model;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Context data containing time-based contextual information with measurements, events, and alarms for a specific time window")
@AllArgsConstructor
@Validated
public class ContextDataApply {

    private ContextData contextData;
    private ContextDataApplyError error;

    public enum ContextDataApplyError {
        ALARM_NOT_FOUND("Alarm doesn't exist!"),
        ALARM_NOT_DEFINED("Alarm is not defined!"),
        ALARM_UPDATE_FAILED("Failed to update alarm with context data!"),
        DEVICE_NOT_FOUND("Device doesn't exist!"),
        CONTEXT_CONFIG_NOT_VALID("Context configuration is not valid! Please check stored context configurations.");

        private String message;

        private ContextDataApplyError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

}
