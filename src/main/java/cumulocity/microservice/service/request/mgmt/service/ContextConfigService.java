package cumulocity.microservice.service.request.mgmt.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;

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
}
