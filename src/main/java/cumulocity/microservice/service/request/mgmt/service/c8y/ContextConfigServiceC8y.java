package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

import ch.qos.logback.core.Context;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.model.ContextData;
import cumulocity.microservice.service.request.mgmt.service.ContextConfigService;

@Service
public class ContextConfigServiceC8y implements ContextConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(ContextConfigServiceC8y.class);

    private final InventoryApi inventoryApi;
    private final AlarmApi alarmApi;
    
    public ContextConfigServiceC8y(InventoryApi inventoryApi, AlarmApi alarmApi) {
        this.inventoryApi = inventoryApi;
        this.alarmApi = alarmApi;
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

    @Override
    public void applyContextConfigsToAlarm(String alarmId) {
        LOG.debug("Applying context configurations to alarm with ID: {}", alarmId);
        
        if (alarmId == null || alarmId.trim().isEmpty()) {
            throw new IllegalArgumentException("Alarm ID cannot be null or empty");
        }
        
        AlarmRepresentation alarm = null;
        try {
            // Get the alarm
            alarm = alarmApi.getAlarm(GId.asGId(alarmId));
            if (alarm == null) {
                throw new RuntimeException("Alarm not found with ID: " + alarmId);
            }
        } catch (SDKException e) {
            if (e.getHttpStatus() == 404) {
                throw new RuntimeException("Alarm not found with ID: " + alarmId, e);
            }
            throw new RuntimeException("Error applying context configurations to alarm: " + alarmId, e);
        } catch (Exception e) {
            LOG.error("Error applying context configurations to alarm {}: {}", alarmId, e.getMessage(), e);
            throw new RuntimeException("Error applying context configurations to alarm: " + alarmId, e);
        }

        applyContextConfigsToAlarm(alarm); 
            
        try {
            // Update the alarm with the context data
            alarmApi.update(alarm);
            LOG.debug("Successfully updated alarm {} with context data", alarm.getId());
        } catch (SDKException e) {
            LOG.error("Failed to update alarm {} with context data: {}", alarm.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update alarm with context data", e);
        }

    }

    @Override
    public void applyContextConfigsToAlarm(AlarmRepresentation alarm) {
        LOG.debug("Applying context configurations to alarm with ID: {}", alarm.getId());
        
        if (alarm == null || alarm.getId() == null) {
            throw new IllegalArgumentException("Alarm or Alarm ID cannot be null");
        }
        
        try {
            // Get the device associated with the alarm
            ManagedObjectRepresentation device = null;
            if (alarm.getSource() != null && alarm.getSource().getId() != null) {
                device = inventoryApi.get(alarm.getSource().getId());
            }
            
            // Get all active context configurations
            List<ContextConfig> activeConfigs = getActiveContextConfigList();
            
            int appliedCount = 0;
            for (ContextConfig config : activeConfigs) {
                if (shouldApplyConfigToAlarm(config, alarm, device)) {
                    // Apply the configuration logic here
                    // This would involve adding context data to the alarm based on the config settings
                    //applyConfigurationToAlarm(config, alarm);
                    ContextData contextData = createContextDataFromConfig(config, alarm.getDateTime());
                    alarm.setProperty("ctx_ContextData", contextData);        
                    appliedCount++;
                    LOG.debug("Applied context configuration {} to alarm {}", config.getId(), alarm.getId());
                }
            }
            
            LOG.info("Applied {} context configurations to alarm {}", appliedCount, alarm.getId());
            
        } catch (SDKException e) {
            throw new RuntimeException("Error applying context configurations to alarm: " + alarm.getId(), e);
        } catch (Exception e) {
            LOG.error("Error applying context configurations to alarm {}: {}", alarm.getId(), e.getMessage(), e);
            throw new RuntimeException("Error applying context configurations to alarm: " + alarm.getId(), e);
        }
    }
    
    /**
     * Checks if a context configuration should be applied to the given alarm and device
     */
    private boolean shouldApplyConfigToAlarm(ContextConfig config, AlarmRepresentation alarm, ManagedObjectRepresentation device) {
        if (config.getApply() == null) {
            LOG.warn("Context configuration {} has no apply rules defined", config.getId());
            return false;
        }
        
        // Check device predicates
        if (config.getApply().getDevicePredicate() != null && !config.getApply().getDevicePredicate().isEmpty()) {
            if (device == null) {
                LOG.debug("No device found for alarm, but device predicates are defined - skipping config {}", config.getId());
                return false;
            }
            
            boolean deviceMatches = config.getApply().getDevicePredicate().stream()
                .allMatch(predicate -> matchesPredicate(predicate, device));
            
            if (!deviceMatches) {
                LOG.debug("Device predicates do not match for config {} and device {}", config.getId(), device.getId());
                return false;
            }
        }
        
        // Check alarm predicates
        if (config.getApply().getAlarmPredicate() != null && !config.getApply().getAlarmPredicate().isEmpty()) {
            boolean alarmMatches = config.getApply().getAlarmPredicate().stream()
                .allMatch(predicate -> matchesPredicate(predicate, alarm));
            
            if (!alarmMatches) {
                LOG.debug("Alarm predicates do not match for config {} and alarm {}", config.getId(), alarm.getId());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a predicate matches the given object (alarm or device)
     */
    private boolean matchesPredicate(ContextConfig.ContextPredicate predicate, Object object) {
        if (predicate.getFragment() == null) {
            LOG.warn("Predicate fragment is null - skipping predicate check");
            return false;
        }
        
        // Get the fragment value from the object
        Object fragmentValue = null;
        if (object instanceof ManagedObjectRepresentation) {
            ManagedObjectRepresentation managedObject = (ManagedObjectRepresentation) object;
            
            switch (predicate.getFragment()) {
                case "id":
                    fragmentValue = managedObject.getId().getValue();
                    break;
                case "name":
                    fragmentValue = managedObject.getName();
                    break;
                case "type":
                    fragmentValue = managedObject.getType();
                    break;
                default:
                    fragmentValue = managedObject.get(predicate.getFragment());
                    break;
            }

        } else if (object instanceof AlarmRepresentation) {
            AlarmRepresentation alarm = (AlarmRepresentation) object;
            switch (predicate.getFragment()) {
                case "id":
                    fragmentValue = alarm.getId().getValue();
                    break;
                case "type":
                    fragmentValue = alarm.getType();
                    break;
                case "status":
                    fragmentValue = alarm.getStatus();
                    break;
                case "severity":
                    fragmentValue = alarm.getSeverity();
                    break;
                default:
                    fragmentValue = alarm.get(predicate.getFragment());
                    break;
            }
        }
        
        if (fragmentValue == null) {
            LOG.debug("Fragment {} not found in object", predicate.getFragment());
            return false;
        }
        
        // If no regex is specified, just check for fragment existence
        if (predicate.getRegex() == null || predicate.getRegex().trim().isEmpty()) {
            LOG.debug("No regex specified for fragment {}, matching on existence", predicate.getFragment());
            return true;
        }
        
        // Apply regex matching
        String fragmentValueStr = fragmentValue.toString();
        try {
            Pattern pattern = Pattern.compile(predicate.getRegex());
            boolean matches = pattern.matcher(fragmentValueStr).matches();
            LOG.debug("Regex {} {} fragment value {} for fragment {}", 
                predicate.getRegex(), matches ? "matches" : "does not match", fragmentValueStr, predicate.getFragment());
            return matches;
        } catch (PatternSyntaxException e) {
            LOG.error("Invalid regex pattern '{}' in predicate for fragment {}: {}", 
                predicate.getRegex(), predicate.getFragment(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Applies the context configuration to the alarm
     * This creates a ContextData object and stores it as a single property on the alarm
     */
    private void applyConfigurationToAlarm(ContextConfig config, AlarmRepresentation alarm) {
        LOG.debug("Applying configuration {} to alarm {}", config.getId(), alarm.getId());
        
        if (config.getConfig() == null) {
            LOG.warn("Context configuration {} has no settings defined - skipping application", config.getId());
            return;
        }
        
        // Create ContextData object
        ContextData contextData = new ContextData();
        contextData.setContextConfigId(config.getId());
        
        // Parse and set date range from relative time strings
        try {
            DateTime dateFrom = parseRelativeDateTime(config.getConfig().getDateFrom(), alarm.getDateTime());
            DateTime dateTo = parseRelativeDateTime(config.getConfig().getDateTo(), alarm.getDateTime());
            
            contextData.setDateFrom(dateFrom);
            contextData.setDateTo(dateTo);
        } catch (Exception e) {
            LOG.error("Failed to parse date range for config {}: {}", config.getId(), e.getMessage());
            // Set default time window (last 24 hours)
            DateTime now = DateTime.now();
            contextData.setDateFrom(now.minusDays(1));
            contextData.setDateTo(now);
        }
        
        // Set data collections
        contextData.setDatapoints(config.getConfig().getDatapoints());
        contextData.setEvents(config.getConfig().getEvents());
        contextData.setAlarms(config.getConfig().getAlarms());
        
        // Store as single property on the alarm (overwriting any existing context data)
        alarm.setProperty("ctx_ContextData", contextData);
        
        try {
            // Update the alarm with the context data
            alarmApi.update(alarm);
            LOG.debug("Successfully updated alarm {} with context data from configuration {}", alarm.getId(), config.getId());
        } catch (SDKException e) {
            LOG.error("Failed to update alarm {} with context data from configuration {}: {}", 
                alarm.getId(), config.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update alarm with context data", e);
        }
    }

    private ContextData createContextDataFromConfig(ContextConfig config, DateTime baseTime) {
        ContextData contextData = new ContextData();
        contextData.setContextConfigId(config.getId());
        
        // Parse and set date range from relative time strings
        try {
            DateTime dateFrom = parseRelativeDateTime(config.getConfig().getDateFrom(), baseTime);
            DateTime dateTo = parseRelativeDateTime(config.getConfig().getDateTo(), baseTime);
            
            contextData.setDateFrom(dateFrom);
            contextData.setDateTo(dateTo);
        } catch (Exception e) {
            LOG.error("Failed to parse date range for config {}: {}", config.getId(), e.getMessage());
            // Set default time window (last 24 hours)
            DateTime now = DateTime.now();
            contextData.setDateFrom(now.minusDays(1));
            contextData.setDateTo(now);
        }
        
        // Set data collections
        contextData.setDatapoints(config.getConfig().getDatapoints());
        contextData.setEvents(config.getConfig().getEvents());
        contextData.setAlarms(config.getConfig().getAlarms());
        
        return contextData;
    }
    
    /**
     * Parses relative date/time strings like "-1d", "0d", "+2h" or ISO 8601 durations like "PT1H", "P1D", "-PT30M" into actual DateTime objects
     */
    private DateTime parseRelativeDateTime(String relativeTime, DateTime baseTime) {
        if (relativeTime == null || relativeTime.trim().isEmpty()) {
            return baseTime;
        }
        
        String trimmed = relativeTime.trim();
        
        // Check for ISO 8601 duration format (e.g., "PT1H", "P1D", "-PT30M")
        if (isIsoDurationFormat(trimmed)) {
            return parseIsoDuration(trimmed, baseTime);
        }
        
        // Check for proprietary format like "-1d", "+2h", "0d"
        if (isProprietaryFormat(trimmed)) {
            return parseProprietaryDuration(trimmed, baseTime);
        }
        
        // Try to parse as ISO datetime format
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return DateTime.parse(trimmed, formatter);
        } catch (Exception e) {
            LOG.warn("Could not parse relative time string '{}', using base time", relativeTime);
            return baseTime;
        }
    }
    
    /**
     * Checks if the string is in proprietary duration format (e.g., "-1d", "+2h", "0d")
     */
    private boolean isProprietaryFormat(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return false;
        }
        
        String lowerTrimmed = timeString.trim().toLowerCase();
        return lowerTrimmed.matches("[+-]?\\d+[dhms]");
    }
    
    /**
     * Parses proprietary duration format and applies it to the base time
     */
    private DateTime parseProprietaryDuration(String proprietaryDuration, DateTime baseTime) {
        String lowerTrimmed = proprietaryDuration.toLowerCase();
        
        boolean isNegative = lowerTrimmed.startsWith("-");
        boolean isPositive = lowerTrimmed.startsWith("+");
        
        String numberPart;
        if (isNegative || isPositive) {
            numberPart = lowerTrimmed.substring(1, lowerTrimmed.length() - 1);
        } else {
            numberPart = lowerTrimmed.substring(0, lowerTrimmed.length() - 1);
        }
        
        char unit = lowerTrimmed.charAt(lowerTrimmed.length() - 1);
        
        try {
            int value = Integer.parseInt(numberPart);
            
            if (isNegative) {
                value = -value;
            }
            
            switch (unit) {
                case 'd':
                    return baseTime.plusDays(value);
                case 'h':
                    return baseTime.plusHours(value);
                case 'm':
                    return baseTime.plusMinutes(value);
                case 's':
                    return baseTime.plusSeconds(value);
                default:
                    LOG.warn("Unknown time unit '{}' in proprietary duration string '{}'", unit, proprietaryDuration);
                    return baseTime;
            }
        } catch (NumberFormatException e) {
            LOG.error("Invalid number format in proprietary duration '{}': {}", proprietaryDuration, e.getMessage());
            return baseTime;
        }
    }
    
    /**
     * Checks if the string is in ISO 8601 duration format
     */
    private boolean isIsoDurationFormat(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return false;
        }
        
        String str = timeString.trim();
        // Handle negative durations
        if (str.startsWith("-")) {
            str = str.substring(1);
        }
        
        // ISO 8601 duration starts with 'P' and may contain 'T' for time components
        return str.startsWith("P") && (str.length() > 1);
    }
    
    /**
     * Parses ISO 8601 duration and applies it to the base time
     */
    private DateTime parseIsoDuration(String isoDuration, DateTime baseTime) {
        boolean isNegative = isoDuration.startsWith("-");
        String durationStr = isNegative ? isoDuration.substring(1) : isoDuration;
        
        try {
            // Try parsing as Period first (for date-based durations like P1D, P1Y2M3D)
            if (!durationStr.contains("T")) {
                Period period = Period.parse(durationStr);
                DateTime result = baseTime.plusYears(period.getYears())
                                         .plusMonths(period.getMonths())
                                         .plusDays(period.getDays());
                return isNegative ? baseTime.minus(result.getMillis() - baseTime.getMillis()) : result;
            } else {
                // Parse as Duration for time-based durations (PT1H, PT30M, P1DT2H30M)
                Duration duration = Duration.parse(durationStr);
                long millis = duration.toMillis();
                return isNegative ? baseTime.minus(millis) : baseTime.plus(millis);
            }
        } catch (DateTimeParseException e) {
            LOG.error("Invalid ISO 8601 duration format '{}': {}", isoDuration, e.getMessage());
            LOG.warn("Using base time due to parsing error");
            return baseTime;
        } catch (Exception e) {
            LOG.error("Error parsing ISO duration '{}': {}", isoDuration, e.getMessage());
            return baseTime;
        }
    }
}
