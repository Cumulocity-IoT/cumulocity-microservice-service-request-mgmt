package cumulocity.microservice.service.request.mgmt.service.c8y;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import cumulocity.microservice.service.request.mgmt.model.ContextConfig;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextApplyRules;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextPredicate;
import cumulocity.microservice.service.request.mgmt.model.ContextConfig.ContextSettings;

class ContextConfigObjectMapperTest {

    @Test
    void testMapToManagedObject() {
        // Arrange
        ContextConfig contextConfig = createSampleContextConfig();
        
        // Act
        ContextConfigObjectMapper mapper = ContextConfigObjectMapper.map2(contextConfig);
        ManagedObjectRepresentation mo = mapper.getManagedObject();
        
        // Assert
        assertNotNull(mo);
        assertEquals(ContextConfigObjectMapper.MANAGEDOBJECT_TYPE, mo.getType());
        assertEquals("Test Context Config", mo.get(ContextConfigObjectMapper.CTX_DESCRIPTION));
        assertEquals(true, mo.get(ContextConfigObjectMapper.CTX_IS_ACTIVE));
        assertNotNull(mo.get(ContextConfigObjectMapper.CTX_APPLY));
        assertNotNull(mo.get(ContextConfigObjectMapper.CTX_CONFIG));
    }

    @Test
    void testMapFromManagedObject() {
        // Arrange
        ContextConfig originalConfig = createSampleContextConfig();
        ContextConfigObjectMapper mapper = ContextConfigObjectMapper.map2("123", originalConfig);
        ManagedObjectRepresentation mo = mapper.getManagedObject();
        
        // Act
        ContextConfig result = ContextConfigObjectMapper.map2(mo);
        
        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Test Context Config", result.getDescription());
        assertTrue(result.getIsActive());
        
        assertNotNull(result.getApply());
        assertNotNull(result.getApply().getDevicePredicate());
        assertEquals(1, result.getApply().getDevicePredicate().size());
        assertEquals("ec_ControlSoftware", result.getApply().getDevicePredicate().get(0).getFragment());
        assertEquals("^CS82$", result.getApply().getDevicePredicate().get(0).getRegex());
        
        assertNotNull(result.getConfig());
        assertEquals("-1d", result.getConfig().getDateFrom());
        assertEquals("0d", result.getConfig().getDateTo());
        assertNotNull(result.getConfig().getDatapoints());
        assertEquals(2, result.getConfig().getDatapoints().size());
        assertTrue(result.getConfig().getDatapoints().contains("MHAI1.H1RMSA1"));
        assertTrue(result.getConfig().getDatapoints().contains("MHAI1.H1RMSA2"));
    }

    @Test
    void testRoundTripMapping() {
        // Arrange
        ContextConfig original = createSampleContextConfig();
        
        // Act - Convert to ManagedObject and back
        ContextConfigObjectMapper mapper1 = ContextConfigObjectMapper.map2("456", original);
        ManagedObjectRepresentation mo = mapper1.getManagedObject();
        ContextConfig result = ContextConfigObjectMapper.map2(mo);
        
        // Assert
        assertNotNull(result);
        assertEquals("456", result.getId());
        assertEquals(original.getDescription(), result.getDescription());
        assertEquals(original.getIsActive(), result.getIsActive());
        
        // Verify apply rules
        assertEquals(original.getApply().getDevicePredicate().size(), 
                    result.getApply().getDevicePredicate().size());
        assertEquals(original.getApply().getAlarmPredicate().size(), 
                    result.getApply().getAlarmPredicate().size());
        
        // Verify config settings
        assertEquals(original.getConfig().getDateFrom(), result.getConfig().getDateFrom());
        assertEquals(original.getConfig().getDateTo(), result.getConfig().getDateTo());
        assertEquals(original.getConfig().getDatapoints().size(), result.getConfig().getDatapoints().size());
    }

    @Test
    void testMapNullManagedObject() {
        // Act
        ManagedObjectRepresentation nullMo = null;
        ContextConfig result = ContextConfigObjectMapper.map2(nullMo);
        
        // Assert
        assertEquals(null, result);
    }

    private ContextConfig createSampleContextConfig() {
        ContextConfig contextConfig = new ContextConfig();
        contextConfig.setDescription("Test Context Config");
        contextConfig.setIsActive(true);
        
        // Create apply rules
        ContextApplyRules applyRules = new ContextApplyRules();
        
        // Device predicates
        List<ContextPredicate> devicePredicates = Arrays.asList(
            createPredicate("ec_ControlSoftware", "^CS82$")
        );
        applyRules.setDevicePredicate(devicePredicates);
        
        // Alarm predicates
        List<ContextPredicate> alarmPredicates = Arrays.asList(
            createPredicate("alarmType", "^ConnectionError$")
        );
        applyRules.setAlarmPredicate(alarmPredicates);
        
        contextConfig.setApply(applyRules);
        
        // Create settings
        ContextSettings settings = new ContextSettings();
        settings.setDateFrom("-1d");
        settings.setDateTo("0d");
        settings.setDatapoints(Arrays.asList("MHAI1.H1RMSA1", "MHAI1.H1RMSA2"));
        settings.setEvents(Arrays.asList("ec_S:242/232", "ec_S:66/11"));
        settings.setAlarms(Arrays.asList("ec_S:242/232"));
        
        contextConfig.setConfig(settings);
        
        return contextConfig;
    }
    
    private ContextPredicate createPredicate(String fragment, String regex) {
        ContextPredicate predicate = new ContextPredicate();
        predicate.setFragment(fragment);
        predicate.setRegex(regex);
        return predicate;
    }
}