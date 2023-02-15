package cumulocity.microservice.service.request.mgmt.service;

import java.util.HashMap;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.service.request.mgmt.model.ServiceRequestAttachment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestComment;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestCommentType;
import cumulocity.microservice.service.request.mgmt.model.ServiceRequestSource;

public class ServiceRequestCommentEventMapper {
	public static final String EVENT_TYPE = "c8y_ServiceRequestComment";
	
	public static final String SR_COMMENT_TYPE = "sr_CommentType";
	public static final String SR_OWNER = "sr_Owner";
	public static final String C8Y_IS_BINARY = "c8y_IsBinary";
	
	private final EventRepresentation event;
	
	public static ServiceRequestCommentEventMapper map2(ServiceRequestComment serviceRequestComment) {
		if(serviceRequestComment == null) {
			return null;
		}
		
		ServiceRequestCommentEventMapper mapper = new ServiceRequestCommentEventMapper();
		mapper.setSource(serviceRequestComment.getSource());
		mapper.setOwner(serviceRequestComment.getOwner());
		mapper.setText(serviceRequestComment.getText());
		mapper.setServiceRequestType(serviceRequestComment.getType());
		return mapper;
	}
	
	public static ServiceRequestComment map2(EventRepresentation event) {
		if(event == null) {
			return null;
		}
		
		ServiceRequestCommentEventMapper mapper = new ServiceRequestCommentEventMapper(event);
		ServiceRequestComment serviceRequestComment = new ServiceRequestComment();
		serviceRequestComment.setCreationTime(mapper.getCreationDateTime());
		serviceRequestComment.setSource(mapper.getSource());
		serviceRequestComment.setId(mapper.getId());
		serviceRequestComment.setLastUpdated(mapper.getLastUpdatedDateTime());
		serviceRequestComment.setOwner(mapper.getOwner());
		serviceRequestComment.setText(mapper.getText());
		serviceRequestComment.setType(mapper.getServiceRequestType());
		serviceRequestComment.setAttachment(mapper.getAttachment());
		return serviceRequestComment;
	}
	
	public ServiceRequestCommentEventMapper() {
		event = new EventRepresentation();
		event.setDateTime(new DateTime());
		event.setType(EVENT_TYPE);
	}
	
	public ServiceRequestCommentEventMapper(Long id) {
		event = new EventRepresentation();
		event.setId(GId.asGId(id));
	}
	
	public ServiceRequestCommentEventMapper(EventRepresentation event) {
		this.event = event;
		this.event.setType(EVENT_TYPE);
	}
	
	public ServiceRequestCommentType getServiceRequestType() {
		return ServiceRequestCommentType.fromValue((String) event.get(SR_COMMENT_TYPE));
	}
	
	public void setServiceRequestType(ServiceRequestCommentType serviceRequestCommentType) {
		event.set(serviceRequestCommentType.toString(), SR_COMMENT_TYPE);
	}
	
	public String getOwner() {
		return (String) event.get(SR_OWNER);
	}
	
	public void setOwner(String owner) {
		event.set(owner, SR_OWNER);
	}
	
	public ServiceRequestSource getSource() {
		return parseSource(event.getSource());
	}
	
	public void setSource(ServiceRequestSource source) {
		ManagedObjectRepresentation sourceMo = new ManagedObjectRepresentation();
		sourceMo.setId(GId.asGId(source.getId()));
		event.setSource(sourceMo);
	}
	
	public String getText() {
		return event.getText();	
	}
	
	public void setText(String title) {
		event.setText(title);
	}
	
	public void setExternalId(String externalId) {
		ExternalIDRepresentation externalIdRepresentation = new ExternalIDRepresentation();
		externalIdRepresentation.setExternalId(externalId);
		event.setExternalSource(externalIdRepresentation);
	}
	
	public DateTime getCreationDateTime() {
		return event.getCreationDateTime();
	}
	
	public DateTime getLastUpdatedDateTime() {
		return event.getLastUpdatedDateTime();
	}
	
	public String getId() {
		if(event.getId() != null) {
			return event.getId().getValue();			
		}
		return null;
	}	
	
	public void setId(Long id) {
		event.setId(GId.asGId(id));
	}
	
	public ServiceRequestAttachment getAttachment() {
		return parseAttachment(event.get(C8Y_IS_BINARY));
	}
	
	public void setAttachment(ServiceRequestAttachment attachment) {
		event.set(attachment, C8Y_IS_BINARY);
	}
	
	public EventRepresentation getEvent() {
		return event;
	}
	
	private ServiceRequestSource parseSource(ManagedObjectRepresentation obj) {
		if(obj == null) {
			return null;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ServiceRequestSource source = new ServiceRequestSource();
		source.setId(obj.getId().getValue());
		source.setSelf(obj.getSelf());
		source.setAdditionalProperty("name", obj.getName());
		return source;
	}
	
	private ServiceRequestAttachment parseAttachment(Object obj) {
		if(obj == null) {
			return null;
		}
		HashMap<String, Object> map = (HashMap<String, Object>) obj;
		ServiceRequestAttachment attachment = new ServiceRequestAttachment();
		attachment.setLength((Long)map.get("length"));
		attachment.setName((String)map.get("name"));
		attachment.setType((String)map.get("type"));
		return attachment;
	}
}