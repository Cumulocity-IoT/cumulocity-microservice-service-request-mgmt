package cumulocity.microservice.service.request.mgmt.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Request list")
@Validated
public class RequestList<T> {

	@Schema(required = true, description = "list element")
	@NotNull
	@Valid
	private List<T> list;
	
	@Schema(required = true, description = "current page", example = "2")
	@NotNull
	@Valid
	private Integer currentPage;
	
	@Schema(required = true, description = "page size", example = "1")
	@NotNull
	@Valid
	private Integer pageSize;
	
	@Schema(required = true, description = "totalPages", example = "3")
	@NotNull
	@Valid
	private Integer totalPages;

	@Schema(required = false, description = "totalElements", example = "83")
	@Valid
	private Integer totalElements;
}
