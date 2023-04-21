package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cumulocity.microservice.api.CumulocityClientProperties;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventAttachmentApi {
		
	private ContextService<MicroserviceCredentials> contextService;
	
	private final CumulocityClientProperties clientProperties;
	
	private EventApi eventApi;
	
	@Autowired
	public EventAttachmentApi(ContextService<MicroserviceCredentials> contextService, CumulocityClientProperties clientProperties, EventApi eventApi) {
		super();
		this.contextService = contextService;
		this.clientProperties = clientProperties;
		this.eventApi = eventApi;
	}
	

	public EventBinary uploadEventAttachment(final BinaryInfo binaryInfo, Resource resource, final String eventId) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(eventId));
		if(event == null) {
			return null;
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("object", binaryInfo, MediaType.APPLICATION_JSON);
		multipartBodyBuilder.part("file", resource, MediaType.TEXT_PLAIN);
		
		MultiValueMap<String,HttpEntity<?>> body = multipartBodyBuilder.build();
		HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(body, headers);

		String serverUrl = clientProperties.getBaseURL() + "/event/events/" + eventId + "/binaries";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<EventBinary> response = restTemplate.postForEntity(serverUrl, requestEntity, EventBinary.class);
		if(response.getStatusCodeValue() >= 300) {
			log.error("Upload event binary failed with http code {}", response.getStatusCode().toString());;
			return null;
		}
		return response.getBody();
	}
	
}
