package cumulocity.microservice.service.request.mgmt.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Context data containing time-based contextual information with measurements, events, and alarms for a specific time window")
@Validated
public class ContextData {

    @Schema(required = true, description = "Start date and time of the context window", example = "2025-09-20T18:12:09.000")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @NotNull
    private DateTime dateFrom;

    @Schema(required = true, description = "End date and time of the context window", example = "2025-09-21T18:12:09.000")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @NotNull
    private DateTime dateTo;

    @Schema(description = "List of measurement datapoints included in this context", example = "[\"MHAI1.H1RMSA1\", \"MHAI1.H1RMSA2\"]")
    private List<String> datapoints;

    @Schema(description = "List of event types included in this context", example = "[\"ec_S:66/11\", \"ec_S:66/11\"]")
    private List<String> events;

    @Schema(description = "List of alarm types included in this context")
    private List<String> alarms;

    @Schema(required = true, description = "ID of the context configuration that generated this context data", example = "123456789")
    private String contextConfigId;
}
