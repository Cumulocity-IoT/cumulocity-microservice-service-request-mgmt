package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cumulocity.microservice.api.CumulocityClientProperties;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RawTextApi {
	private ContextService<MicroserviceCredentials> contextService;

    private final String alarmPath;

    private final String eventPath;

    public RawTextApi(ContextService<MicroserviceCredentials> contextService, CumulocityClientProperties clientProperties) {
        this.contextService = contextService;
        this.alarmPath = clientProperties.getBaseURL() + "/alarm/alarms";
        this.eventPath = clientProperties.getBaseURL() + "/event/events";
    }

    public AlarmRepresentation createAlarm(String jsonContent) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonContent, headers);

		RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<AlarmRepresentation> response;
        try {
            response = restTemplate.postForEntity(alarmPath, request, AlarmRepresentation.class);
        } catch (Exception e) {
            log.error("Creating alarm failed! json: {}", jsonContent, e);
            return null;
        }

        log.debug("Creating alarm returns status code: {}", response.getStatusCodeValue());
        return response.getBody();
    }

    public EventRepresentation createEvent(String jsonContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonContent, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<EventRepresentation> response;
        try {
            response = restTemplate.postForEntity(eventPath, request, EventRepresentation.class);
        } catch (Exception e) {
            log.error("Creating event failed! json: {}", jsonContent, e);
            return null;
        }


        log.debug("Creating event returns status code: {}", response.getStatusCodeValue());
        return response.getBody();
    }

}
