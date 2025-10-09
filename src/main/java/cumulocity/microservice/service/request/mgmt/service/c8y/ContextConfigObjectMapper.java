package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextApplyRules;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextPredicate;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextSettings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextConfigObjectMapper {
    public static final String MANAGEDOBJECT_TYPE = "ctx_Config";
    public static final String ALARM_FRAGMENT = "ctx_Data";
    
    public static final String CTX_ID = "id";
    public static final String CTX_DESCRIPTION = "ctx_Description";
    public static final String CTX_APPLY = "ctx_Apply";
    public static final String CTX_CONFIG = "ctx_Config";
    public static final String CTX_IS_ACTIVE = "ctx_IsActive";
    
    // Apply rules fields
    public static final String CTX_DEVICE_PREDICATE = "devicePredicate";
    public static final String CTX_ALARM_PREDICATE = "alarmPredicate";
    
    // Predicate fields
    public static final String CTX_FRAGMENT = "fragment";
    public static final String CTX_REGEX = "regex";
    
    // Settings fields
    public static final String CTX_DATE_FROM = "dateFrom";
    public static final String CTX_DATE_TO = "dateTo";
    public static final String CTX_DATAPOINTS = "datapoints";
    public static final String CTX_EVENTS = "events";
    public static final String CTX_ALARMS = "alarms";
    
    private final ManagedObjectRepresentation managedObject;

    public ContextConfigObjectMapper(ManagedObjectRepresentation managedObject) {
        this.managedObject = managedObject;
        this.managedObject.setType(MANAGEDOBJECT_TYPE);
    }
    
    public ContextConfigObjectMapper() {
        this.managedObject = new ManagedObjectRepresentation();
        this.managedObject.setType(MANAGEDOBJECT_TYPE);
    }
    
    public ContextConfigObjectMapper(String id) {
        this.managedObject = new ManagedObjectRepresentation();
        this.managedObject.setType(MANAGEDOBJECT_TYPE);
        this.managedObject.setId(GId.asGId(id));
    }

    public ManagedObjectRepresentation getManagedObject() {
        return managedObject;
    }
    
    public static ContextConfigObjectMapper map2(ContextConfig contextConfig) {
        ContextConfigObjectMapper mapper = new ContextConfigObjectMapper();
        mapper.setContextConfig(contextConfig);
        return mapper;
    }
    
    public static ContextConfigObjectMapper map2(String id, ContextConfig contextConfig) {
        ContextConfigObjectMapper mapper = new ContextConfigObjectMapper(id);
        mapper.setContextConfig(contextConfig);
        return mapper;
    }
    
    public static ContextConfig map2(ManagedObjectRepresentation managedObject) {
        if (managedObject == null) {
            return null;
        }
        ContextConfigObjectMapper mapper = new ContextConfigObjectMapper(managedObject);
        return mapper.getContextConfig();
    }
    
    @SuppressWarnings("unchecked")
    public ContextConfig getContextConfig() {
        ContextConfig contextConfig = new ContextConfig();
        
        // Set basic fields
        if (managedObject.getId() != null) {
            contextConfig.setId(managedObject.getId().getValue());
        }
        contextConfig.setDescription((String) managedObject.get(CTX_DESCRIPTION));
        contextConfig.setIsActive((Boolean) managedObject.get(CTX_IS_ACTIVE));
        
        // Parse apply rules
        Object applyObj = managedObject.get(CTX_APPLY);
        if (applyObj instanceof Map) {
            ContextApplyRules apply = parseApplyRules((Map<String, Object>) applyObj);
            contextConfig.setApply(apply);
        }
        
        // Parse config settings
        Object configObj = managedObject.get(CTX_CONFIG);
        if (configObj instanceof Map) {
            ContextSettings config = parseContextSettings((Map<String, Object>) configObj);
            contextConfig.setConfig(config);
        }
        
        return contextConfig;
    }
    
    public void setContextConfig(ContextConfig contextConfig) {
        if (contextConfig.getId() != null && managedObject.getId() == null) {
            managedObject.setId(GId.asGId(contextConfig.getId()));
        }
        
        managedObject.set(contextConfig.getDescription(), CTX_DESCRIPTION);
        managedObject.set(contextConfig.getIsActive(), CTX_IS_ACTIVE);
        
        // Set apply rules
        if (contextConfig.getApply() != null) {
            Map<String, Object> applyMap = convertApplyRulesToMap(contextConfig.getApply());
            managedObject.set(applyMap, CTX_APPLY);
        }
        
        // Set config settings
        if (contextConfig.getConfig() != null) {
            Map<String, Object> configMap = convertContextSettingsToMap(contextConfig.getConfig());
            managedObject.set(configMap, CTX_CONFIG);
        }
    }

    public String getId() {
        if (managedObject.getId() != null) {
            return managedObject.getId().getValue();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private ContextApplyRules parseApplyRules(Map<String, Object> applyMap) {
        ContextApplyRules apply = new ContextApplyRules();
        
        Object devicePredicateObj = applyMap.get(CTX_DEVICE_PREDICATE);
        if (devicePredicateObj instanceof List) {
            List<ContextPredicate> devicePredicates = parsePredicateList((List<Map<String, Object>>) devicePredicateObj);
            apply.setDevicePredicate(devicePredicates);
        }
        
        Object alarmPredicateObj = applyMap.get(CTX_ALARM_PREDICATE);
        if (alarmPredicateObj instanceof List) {
            List<ContextPredicate> alarmPredicates = parsePredicateList((List<Map<String, Object>>) alarmPredicateObj);
            apply.setAlarmPredicate(alarmPredicates);
        }
        
        return apply;
    }
    
    private List<ContextPredicate> parsePredicateList(List<Map<String, Object>> predicateList) {
        List<ContextPredicate> predicates = new ArrayList<>();
        for (Map<String, Object> predicateMap : predicateList) {
            ContextPredicate predicate = new ContextPredicate();
            predicate.setFragment((String) predicateMap.get(CTX_FRAGMENT));
            predicate.setRegex((String) predicateMap.get(CTX_REGEX));
            predicates.add(predicate);
        }
        return predicates;
    }
    
    @SuppressWarnings("unchecked")
    private ContextSettings parseContextSettings(Map<String, Object> configMap) {
        ContextSettings config = new ContextSettings();
        
        config.setDateFrom((String) configMap.get(CTX_DATE_FROM));
        config.setDateTo((String) configMap.get(CTX_DATE_TO));
        
        Object datapointsObj = configMap.get(CTX_DATAPOINTS);
        if (datapointsObj instanceof List) {
            config.setDatapoints((List<String>) datapointsObj);
        }
        
        Object eventsObj = configMap.get(CTX_EVENTS);
        if (eventsObj instanceof List) {
            config.setEvents((List<String>) eventsObj);
        }
        
        Object alarmsObj = configMap.get(CTX_ALARMS);
        if (alarmsObj instanceof List) {
            config.setAlarms((List<String>) alarmsObj);
        }
        
        return config;
    }
    
    private Map<String, Object> convertApplyRulesToMap(ContextApplyRules apply) {
        Map<String, Object> applyMap = new HashMap<>();
        
        if (apply.getDevicePredicate() != null) {
            List<Map<String, Object>> devicePredicates = convertPredicateListToMap(apply.getDevicePredicate());
            applyMap.put(CTX_DEVICE_PREDICATE, devicePredicates);
        }
        
        if (apply.getAlarmPredicate() != null) {
            List<Map<String, Object>> alarmPredicates = convertPredicateListToMap(apply.getAlarmPredicate());
            applyMap.put(CTX_ALARM_PREDICATE, alarmPredicates);
        }
        
        return applyMap;
    }
    
    private List<Map<String, Object>> convertPredicateListToMap(List<ContextPredicate> predicates) {
        List<Map<String, Object>> predicateList = new ArrayList<>();
        for (ContextPredicate predicate : predicates) {
            Map<String, Object> predicateMap = new HashMap<>();
            predicateMap.put(CTX_FRAGMENT, predicate.getFragment());
            predicateMap.put(CTX_REGEX, predicate.getRegex());
            predicateList.add(predicateMap);
        }
        return predicateList;
    }
    
    private Map<String, Object> convertContextSettingsToMap(ContextSettings config) {
        Map<String, Object> configMap = new HashMap<>();
        
        configMap.put(CTX_DATE_FROM, config.getDateFrom());
        configMap.put(CTX_DATE_TO, config.getDateTo());
        
        if (config.getDatapoints() != null) {
            configMap.put(CTX_DATAPOINTS, config.getDatapoints());
        }
        
        if (config.getEvents() != null) {
            configMap.put(CTX_EVENTS, config.getEvents());
        }
        
        if (config.getAlarms() != null) {
            configMap.put(CTX_ALARMS, config.getAlarms());
        }
        
        return configMap;
    }
}