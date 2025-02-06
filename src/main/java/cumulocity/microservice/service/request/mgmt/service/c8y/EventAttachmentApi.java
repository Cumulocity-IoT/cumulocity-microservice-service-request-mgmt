package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
	

	/**
	 * Uploads an attachment to an event.
	 * 
	 * @param binaryInfo
	 * @param resource
	 * @param eventId
	 * @param overwrites
	 * @return response status code
	 */
	public int uploadEventAttachment(final BinaryInfo binaryInfo, Resource resource, final String eventId, boolean overwrites) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(eventId));
		if(event == null) {
			return 404;
		}
		
		//TODO Before sending this data to cumulocity an validation should be done: file size, does the content type fit etc.
		
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

		ResponseEntity<EventBinary> response;
		if (overwrites) {
			response = restTemplate.exchange(serverUrl, HttpMethod.PUT, requestEntity, EventBinary.class);
		} else {
			response = restTemplate.postForEntity(serverUrl, requestEntity, EventBinary.class);
		}

		if(response.getStatusCodeValue() >= 300) {
			log.error("Upload event binary failed with http code {}", response.getStatusCode().toString());;
		}
		return response.getStatusCodeValue();
	}
	
	public EventAttachment downloadEventAttachment(final String eventId) {
		EventRepresentation event = eventApi.getEvent(GId.asGId(eventId));
		if(event == null) {
			return null;
		}
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());

		String serverUrl = clientProperties.getBaseURL() + "/event/events/" + eventId + "/binaries";
		RestTemplate restTemplate = new RestTemplate();
		
		
		EventAttachment attachment = restTemplate.execute(serverUrl, HttpMethod.GET, clientHttpRequest -> {
			clientHttpRequest.getHeaders().set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());
		}, clientHttpResponse -> {
			//TODO currently the byte array of file is stored in memory, better solution would be to use a stream.
			EventAttachment eventAttachment = new EventAttachment();
			
			eventAttachment.setContentDispostion(clientHttpResponse.getHeaders().getContentDisposition());
			eventAttachment.setContentType(clientHttpResponse.getHeaders().getContentType());
			eventAttachment.setAttachment(clientHttpResponse.getBody().readAllBytes());
			
			clientHttpResponse.getRawStatusCode();
			clientHttpResponse.getStatusText();
			log.info("Download event attachment response; HTTP StatusCode: {}, Text: {}", clientHttpResponse.getRawStatusCode(), clientHttpResponse.getStatusText());
			return eventAttachment;
		});
		
		return attachment;
	}
	
}
