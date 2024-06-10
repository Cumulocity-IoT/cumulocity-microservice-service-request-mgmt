package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cumulocity.microservice.api.CumulocityClientProperties;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DebugEventApi {

    	private ContextService<MicroserviceCredentials> contextService;
	
	    private final CumulocityClientProperties clientProperties;

        @Autowired
        public DebugEventApi(ContextService<MicroserviceCredentials> contextService, CumulocityClientProperties clientProperties) {
            super();
            this.contextService = contextService;
            this.clientProperties = clientProperties;
        }

        public void test() {
            HttpHeaders headers = new HttpHeaders();
		    headers.set("Authorization", contextService.getContext().toCumulocityCredentials().getAuthenticationString());
		    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            String serverUrl = clientProperties.getBaseURL() + "/event/events?fragmentValue=NEW&pageSize=2000&fragmentType=sr_SyncStatus&type=c8y_ServiceRequest";
		    RestTemplate restTemplate = new RestTemplate();
            
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(serverUrl, HttpMethod.GET, entity, String.class);

            log.info("Response: {}", response.getBody());
        }
}
