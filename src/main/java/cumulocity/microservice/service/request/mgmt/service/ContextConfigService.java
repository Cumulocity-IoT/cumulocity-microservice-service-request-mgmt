package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.model.ContextDataApply;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface ContextConfigService {

    /**
     * Creates a new context configuration
     * 
     * @param contextConfig the context configuration to create
     * @return the created context configuration with assigned ID
     */
    ContextConfig createContextConfig(@Valid @NotNull ContextConfig contextConfig);

    /**
     * Updates an existing context configuration
     * 
     * @param contextConfig the context configuration to update
     * @return the updated context configuration
     */
    ContextConfig updateContextConfig(@Valid @NotNull ContextConfig contextConfig);

    /**
     * Retrieves all context configurations
     * 
     * @return list of all context configurations
     */
    List<ContextConfig> getContextConfigList();

    /**
     * Retrieves a specific context configuration by ID
     * 
     * @param configId the ID of the context configuration
     * @return optional containing the context configuration if found
     */
    Optional<ContextConfig> getContextConfig(@NotNull String configId);

    /**
     * Deletes a context configuration by ID
     * 
     * @param configId the ID of the context configuration to delete
     */
    void deleteContextConfig(@NotNull String configId);

    /**
     * Checks if a context configuration exists by ID
     * 
     * @param configId the ID of the context configuration
     * @return true if the configuration exists, false otherwise
     */
    boolean existsContextConfig(@NotNull String configId);

    /**
     * Retrieves active context configurations only
     * 
     * @return list of active context configurations
     */
    List<ContextConfig> getActiveContextConfigList();

    /**
     * Applies all matching context configurations to a specific alarm
     * 
     * @param alarmId the ID of the alarm to apply context configurations to
     * @return result of applying context configurations, including any errors
     */
    ContextDataApply applyContextConfigsToAlarm(@NotNull String alarmId);

    /**
     * Applies all matching context configurations to a specific alarm. The alarm will not be updated in that method, it will only add this to the alarm given as parameter.
     * 
     * @param alarm the alarm representation to apply context configurations to
     * @return result of applying context configurations, including any errors
     */
    ContextDataApply applyContextConfigsToAlarm(AlarmRepresentation alarm);

    /**
     * Applies all matching context configurations to a specific alarm by alarm ID, using provided user and microservice credentials for context switching.
     * 
     * @param alarmId the ID of the alarm to apply context configurations to
     * @param credentials user credentials for context switching
     * @param microserviceCredentials microservice credentials for context switching
     */
    void applyContextConfigsToAlarm(String alarmId, MicroserviceCredentials microserviceCredentials);
}
