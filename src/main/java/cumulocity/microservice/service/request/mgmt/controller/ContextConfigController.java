package cumulocity.microservice.service.request.mgmt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.model.ContextData;
import cumulocity.microservice.service.request.mgmt.model.ContextDataApply;
import cumulocity.microservice.service.request.mgmt.service.ContextConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/context/config")
public class ContextConfigController {

    private ContextConfigService contextConfigService;

    public ContextConfigController(ContextConfigService contextConfigService) {
        this.contextConfigService = contextConfigService;
    }

    @Operation(summary = "CREATE context configuration", description = "Creates a new context configuration for alarm data context.", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContextConfig.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextConfig> createContextConfig(@RequestBody ContextConfig contextConfig) {
        ContextConfig newContextConfig = contextConfigService.createContextConfig(contextConfig);
        return new ResponseEntity<ContextConfig>(newContextConfig, HttpStatus.CREATED);
    }

    @Operation(summary = "UPDATE context configuration", description = "Updates an existing context configuration.", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContextConfig.class))),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    @PutMapping(path = "/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextConfig> updateContextConfig(@PathVariable String configId,
            @RequestBody ContextConfig contextConfig) {
        Optional<ContextConfig> existingConfig = contextConfigService.getContextConfig(configId);
        if (existingConfig.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        contextConfig.setId(configId);
        ContextConfig updatedContextConfig = contextConfigService.updateContextConfig(contextConfig);
        return new ResponseEntity<ContextConfig>(updatedContextConfig, HttpStatus.OK);
    }

    @Operation(summary = "GET all context configurations", description = "Returns complete list of context configurations", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContextConfig.class)))) })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextConfig>> getContextConfigList() {
        List<ContextConfig> contextConfigList = contextConfigService.getContextConfigList();
        return new ResponseEntity<List<ContextConfig>>(contextConfigList, HttpStatus.OK);
    }

    @Operation(summary = "GET context configuration by Id", description = "Returns specific context configuration by Id", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContextConfig.class))),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @GetMapping(path = "/{configId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContextConfig> getContextConfigById(@PathVariable String configId) {
        Optional<ContextConfig> contextConfig = contextConfigService.getContextConfig(configId);
        if (contextConfig.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ContextConfig>(contextConfig.get(), HttpStatus.OK);
    }

    @Operation(summary = "DELETE context configuration by Id", description = "Deletes a context configuration by Id", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @DeleteMapping(path = "/{configId}")
    public ResponseEntity<Void> deleteContextConfigById(@PathVariable String configId) {
        Optional<ContextConfig> contextConfig = contextConfigService.getContextConfig(configId);
        if (contextConfig.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        contextConfigService.deleteContextConfig(configId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "APPLY context configurations to alarm", description = "Applies all matching context configurations to a specific alarm by alarm ID.", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK - Context configurations applied successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContextData.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Alarm not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Context configuration is not valid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseBody.class))) })
    @PostMapping(path = "/apply/alarm/{alarmId}")
    public ResponseEntity<?> applyContextConfigsToAlarm(@PathVariable String alarmId) {
         ContextDataApply applyContextConfigsToAlarm = contextConfigService.applyContextConfigsToAlarm(alarmId);

        switch (applyContextConfigsToAlarm.getError()) {
            case ALARM_NOT_DEFINED:
                return new ResponseEntity<ErrorResponseBody>(new ErrorResponseBody(applyContextConfigsToAlarm.getError()), HttpStatus.BAD_REQUEST);
            case ALARM_NOT_FOUND:
                return new ResponseEntity<ErrorResponseBody>(new ErrorResponseBody(applyContextConfigsToAlarm.getError()), HttpStatus.NOT_FOUND);
            case CONTEXT_CONFIG_NOT_VALID:
                return new ResponseEntity<ErrorResponseBody>(new ErrorResponseBody(applyContextConfigsToAlarm.getError()), HttpStatus.INTERNAL_SERVER_ERROR);
            default:
                break;
        }

        return new ResponseEntity<>(applyContextConfigsToAlarm.getContextData(), HttpStatus.OK);
    }
}
