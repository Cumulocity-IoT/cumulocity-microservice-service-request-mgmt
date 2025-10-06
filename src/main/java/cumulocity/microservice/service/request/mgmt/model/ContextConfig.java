package cumulocity.microservice.service.request.mgmt.model;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Context Configuration object that defines rules and settings for creating alarm contexts in Data Explorer. Contains device and measurement predicates to determine when to apply the configuration, and the actual context settings including time windows and data points.")
@Validated
public class ContextConfig {

    @Schema(required = true, description = "Unique identifier for the context configuration", example = "wind-turbine-connection-error-context")
    @NotNull
    private String id;

    @Schema(required = true, description = "Human readable name for the context configuration", example = "Wind Turbine Connection Error Context")
    @NotNull
    private String name;

    @Schema(description = "Description of what this context configuration is used for", example = "Context configuration for CS82 control software connection errors")
    private String description;

    @Schema(required = true, description = "Application rules that define when this context configuration should be applied")
    @NotNull
    @Valid
    private ContextApplyRules apply;

    @Schema(required = true, description = "Context configuration settings including time windows and data selections")
    @NotNull
    @Valid
    private ContextSettings config;

    @Schema(description = "Whether this context configuration is active and should be used", example = "true")
    private Boolean isActive = true;

    @Data
    @Schema(description = "Rules that define when this context configuration should be applied to an alarm")
    public static class ContextApplyRules {

        @Schema(description = "Device predicates that must match for this context to be applied")
        @Valid
        private List<ContextPredicate> devicePredicate;

        @Schema(description = "Measurement predicates that must match for this context to be applied")
        @Valid
        private List<ContextPredicate> meaPredicate;

        @Schema(description = "Alarm predicates that must match for this context to be applied")
        @Valid
        private List<ContextPredicate> alarmPredicate;
    }

    @Data
    @Schema(description = "Predicate for matching objects based on fragment values")
    public static class ContextPredicate {

        @Schema(required = true, description = "Fragment name to check", example = "ec_ControlSoftware")
        @NotNull
        private String fragment;

        @Schema(description = "Value that the fragment must equal", example = "CS82")
        private String equals;

        @Schema(description = "Value that the fragment must contain")
        private String contains;

        @Schema(description = "Regular expression that the fragment must match")
        private String regex;
    }

    @Data
    @Schema(description = "Context configuration settings that define the data and time window")
    public static class ContextSettings {

        @Schema(required = true, description = "Relative start time for the context window", example = "-1d")
        @NotNull
        private String dateFrom;

        @Schema(required = true, description = "Relative end time for the context window", example = "0d")
        @NotNull
        private String dateTo;

        @Schema(description = "List of measurement data points to include in the context", example = "[\"MHAI1.H1RMSA1\", \"MHAI1.H1RMSA2\"]")
        private List<String> datapoints;

        @Schema(description = "List of event types to include in the context", example = "[\"ec_S:242/232\", \"ec_S:66/11\"]")
        private List<String> events;

        @Schema(description = "List of alarm types to include in the context", example = "[\"ec_S:242/232\"]")
        private List<String> alarms;
    }
}
