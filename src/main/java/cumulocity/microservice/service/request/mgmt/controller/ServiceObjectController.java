package cumulocity.microservice.service.request.mgmt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cumulocity.microservice.service.request.mgmt.model.ServiceObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/service/object")
public class ServiceObjectController {

	@Operation(summary = "Add new service object.", description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "404", description = "Not found")})
	@PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceObject> createServiceObject(@RequestBody ServiceObject serviceObject) {
		ServiceObject dummy = new ServiceObject();
		return new ResponseEntity<ServiceObject>(dummy, HttpStatus.OK);
	}
	
	@Operation(summary = "GET service object by Id", description = "Returns service object by internal Id", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@GetMapping(path = "/{serviceObjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceObject> getServiceObjecttById(@PathVariable Long serviceObjectId) {
		ServiceObject serviceObject = new ServiceObject();
		if(serviceObject == null) {
			return new ResponseEntity<ServiceObject>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ServiceObject>(serviceObject, HttpStatus.OK);
	}
	
}
