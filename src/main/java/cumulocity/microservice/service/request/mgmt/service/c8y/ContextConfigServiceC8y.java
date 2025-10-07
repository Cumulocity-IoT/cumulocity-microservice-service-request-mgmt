package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.service.ContextConfigService;

@Service
public class ContextConfigServiceC8y implements ContextConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(ContextConfigServiceC8y.class);

    private final InventoryApi inventoryApi;
    
    public ContextConfigServiceC8y(InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    @Override
    public ContextConfig createContextConfig(ContextConfig contextConfig) {
        LOG.debug("Creating new context configuration: {}", contextConfig.getDescription());
        
        ContextConfigObjectMapper mapper = ContextConfigObjectMapper.map2(contextConfig);
        ManagedObjectRepresentation createdObject = inventoryApi.create(mapper.getManagedObject());
        
        return ContextConfigObjectMapper.map2(createdObject);
    }

    @Override
    public ContextConfig updateContextConfig(ContextConfig contextConfig) {
        LOG.debug("Updating context configuration with ID: {}", contextConfig.getId());
        
        if (contextConfig.getId() == null) {
            throw new IllegalArgumentException("Context configuration ID cannot be null for update operation");
        }
        
        ContextConfigObjectMapper mapper = ContextConfigObjectMapper.map2(contextConfig.getId(), contextConfig);
        ManagedObjectRepresentation updatedObject = inventoryApi.update(mapper.getManagedObject());
        
        return ContextConfigObjectMapper.map2(updatedObject);
    }

    @Override
    public List<ContextConfig> getContextConfigList() {
        LOG.debug("Retrieving all context configurations");
        
        List<ContextConfig> contextConfigs = new ArrayList<>();
        InventoryFilter filter = new InventoryFilter();
        filter.byType(ContextConfigObjectMapper.MANAGEDOBJECT_TYPE);
        
        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjectsByFilter(filter);
        
        for (ManagedObjectRepresentation managedObject : managedObjects.get()) {
            ContextConfig contextConfig = ContextConfigObjectMapper.map2(managedObject);
            if (contextConfig != null) {
                contextConfigs.add(contextConfig);
            }
        }
        
        return contextConfigs;
    }

    @Override
    public Optional<ContextConfig> getContextConfig(String configId) {
        LOG.debug("Retrieving context configuration with ID: {}", configId);
        
        try {
            ManagedObjectRepresentation managedObject = inventoryApi.get(com.cumulocity.model.idtype.GId.asGId(configId));
            
            if (managedObject != null && ContextConfigObjectMapper.MANAGEDOBJECT_TYPE.equals(managedObject.getType())) {
                ContextConfig contextConfig = ContextConfigObjectMapper.map2(managedObject);
                return Optional.ofNullable(contextConfig);
            }
        } catch (Exception e) {
            LOG.warn("Context configuration with ID {} not found: {}", configId, e.getMessage());
        }
        
        return Optional.empty();
    }

    @Override
    public void deleteContextConfig(String configId) {
        LOG.debug("Deleting context configuration with ID: {}", configId);
        
        try {
            inventoryApi.delete(com.cumulocity.model.idtype.GId.asGId(configId));
            LOG.info("Context configuration with ID {} deleted successfully", configId);
        } catch (Exception e) {
            LOG.error("Failed to delete context configuration with ID {}: {}", configId, e.getMessage());
            throw new RuntimeException("Failed to delete context configuration", e);
        }
    }

    @Override
    public boolean existsContextConfig(String configId) {
        LOG.debug("Checking existence of context configuration with ID: {}", configId);
        
        return getContextConfig(configId).isPresent();
    }

    @Override
    public List<ContextConfig> getActiveContextConfigList() {
        LOG.debug("Retrieving active context configurations");
        
        List<ContextConfig> allConfigs = getContextConfigList();
        List<ContextConfig> activeConfigs = new ArrayList<>();
        
        for (ContextConfig config : allConfigs) {
            if (Boolean.TRUE.equals(config.getIsActive())) {
                activeConfigs.add(config);
            }
        }
        
        return activeConfigs;
    }
}
