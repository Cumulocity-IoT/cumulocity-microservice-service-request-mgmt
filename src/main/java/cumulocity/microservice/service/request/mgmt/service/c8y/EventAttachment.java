package cumulocity.microservice.service.request.mgmt.service.c8y;

import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;

import lombok.Data;

@Data
public class EventAttachment {

	private byte[] attachment;
	
	private ContentDisposition contentDispostion;
	
	private MediaType contentType;
}
