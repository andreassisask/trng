package org.trng.service.impl;

import java.util.Map;

import org.trng.format.RandomFormat;
import org.trng.service.RequestParameter;
import org.trng.service.RequestParameterParser;
import org.trng.service.RequestParameterParserFactory;
import org.trng.service.ServiceRequest;
import org.trng.service.exception.InvalidServiceRequestException;

public abstract class AbstractServiceRequest implements ServiceRequest {
	private RandomFormat format;
	private Integer quantity;

	public RandomFormat getFormat() {
		return format;
	}

	public void setFormat(RandomFormat format) {
		this.format = format;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void validate() throws InvalidServiceRequestException {
		validateQuantity();
		validateFormat();
	}

	public void setParameters(Map<String, String[]> parameters) throws InvalidServiceRequestException {
		setParameters(parameters, new DefaultRequestParameterParserFactory());
	}

	@SuppressWarnings("unchecked")
	public void setParameters(Map<String, String[]> parameters, RequestParameterParserFactory factory)
			throws InvalidServiceRequestException {
		String[] v;

		v = parameters.get(RequestParameter.FORMAT.getName());
		if (v != null && v.length == 1) {
			RequestParameterParser<RandomFormat> processor = (RequestParameterParser<RandomFormat>) factory
					.getProcessor(RequestParameter.FORMAT);
			setFormat(processor.processParameterValue(v[0]));
		}

		v = parameters.get(RequestParameter.QUANTITY.getName());
		if (v != null && v.length == 1) {
			RequestParameterParser<Integer> processor = (RequestParameterParser<Integer>) factory
					.getProcessor(RequestParameter.QUANTITY);
			setQuantity(processor.processParameterValue(v[0]));
		}
	}

	protected void validateFormat() throws InvalidServiceRequestException {
		if (format == null)
			throw new InvalidServiceRequestException("Format is missing or invalid");

	}

	protected void validateQuantity() throws InvalidServiceRequestException {
		if (quantity == null || quantity <= 0)
			throw new InvalidServiceRequestException("Quantity must be greater than zero");
	}

}
