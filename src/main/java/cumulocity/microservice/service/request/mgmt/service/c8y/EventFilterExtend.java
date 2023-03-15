package cumulocity.microservice.service.request.mgmt.service.c8y;

import java.util.Date;

import com.cumulocity.model.DateConverter;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.util.ExtensibilityConverter;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.ParamSource;
import com.cumulocity.sdk.client.event.EventFilter;

public class EventFilterExtend extends EventFilter {

	@ParamSource
	private String fragmentType;

	@ParamSource
	private String fragmentValue;

	@ParamSource
	private String dateFrom;

	@ParamSource
	private String dateTo;

	@ParamSource
	private String createdFrom;

	@ParamSource
	private String createdTo;

	@ParamSource
	private String type;

	@ParamSource
	private String source;

	@ParamSource
	private String withSourceAssets;

	@ParamSource
	private String withSourceDevices;

	/**
	 * Specifies the {@code type} query parameter
	 *
	 * @param type the type of the event(s)
	 * @return the event filter with {@code type} set
	 */
	public EventFilterExtend byType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Specifies the {@code source} query parameter
	 *
	 * @param id the managed object id that generated the event(s)
	 * @return the event filter with {@code source} set
	 */
	public EventFilterExtend bySource(GId id) {
		this.source = id.getValue();
		return this;
	}

	/**
	 * Specifies the {@code source} query parameter
	 *
	 * @param source the managed object that generated the event(s)
	 * @return the event filter with {@code source} set
	 */
	@Deprecated
	public EventFilterExtend bySource(ManagedObjectRepresentation source) {
		this.source = source.getId().getValue();
		return this;
	}

	/**
	 * @return the {@code type} parameter of the query
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the {@code source} parameter of the query
	 */
	public String getSource() {
		return source;
	}

	public EventFilterExtend byFragmentType(Class<?> fragmentType) {
		this.fragmentType = ExtensibilityConverter.classToStringRepresentation(fragmentType);
		return this;
	}

	public EventFilterExtend byFragmentType(String fragmentType) {
		this.fragmentType = fragmentType;
		return this;
	}

	public EventFilterExtend byFragmentValue(String fragmentValue) {
		this.fragmentValue = fragmentValue;
		return this;
	}

	public String getFragmentType() {
		return fragmentType;
	}

	public String getFragmentValue() {
		return fragmentValue;
	}

	public EventFilterExtend byDate(Date fromDate, Date toDate) {
		this.dateFrom = DateConverter.date2String(fromDate);
		this.dateTo = DateConverter.date2String(toDate);
		return this;
	}

	public EventFilterExtend byFromDate(Date fromDate) {
		this.dateFrom = DateConverter.date2String(fromDate);
		return this;
	}

	public String getFromDate() {
		return dateFrom;
	}

	public String getToDate() {
		return dateTo;
	}

	public String getCreatedFrom() {
		return createdFrom;
	}

	public String getCreatedTo() {
		return createdTo;
	}

	public EventFilterExtend byCreationDate(Date fromDate, Date toDate) {
		this.createdFrom = DateConverter.date2String(fromDate);
		this.createdTo = DateConverter.date2String(toDate);
		return this;
	}

	public EventFilterExtend byFromCreationDate(Date fromDate) {
		this.createdFrom = DateConverter.date2String(fromDate);
		return this;
	}

	public Boolean getWithSourceAssets() {
		return Boolean.valueOf(withSourceAssets);

	}

	public EventFilterExtend setWithSourceAssets(Boolean withSourceAssets) {
		this.withSourceAssets = withSourceAssets.toString();
		return this;
	}

	public Boolean getWithSourceDevices() {
		return Boolean.valueOf(withSourceDevices);
	}

	public EventFilterExtend setWithSourceDevices(Boolean withSourceDevices) {
		this.withSourceDevices = withSourceDevices.toString();
		return this;
	}

}
