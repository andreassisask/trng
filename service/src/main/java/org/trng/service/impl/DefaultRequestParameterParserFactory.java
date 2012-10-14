package org.trng.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.trng.service.RequestParameter;
import org.trng.service.RequestParameterParser;
import org.trng.service.RequestParameterParserFactory;

public class DefaultRequestParameterParserFactory implements RequestParameterParserFactory {
	private Map<RequestParameter, RequestParameterParser<?>> processors;

	public DefaultRequestParameterParserFactory() {
		processors = new HashMap<RequestParameter, RequestParameterParser<?>>();
		processors.put(RequestParameter.QUANTITY, new QuantityParser());
		processors.put(RequestParameter.FORMAT, new FormatParser());
	}

	/* (non-Javadoc)
	 * @see org.trng.service.impl.RequestParameterProcessorFactory#getProcessor(org.trng.service.RequestParameter)
	 */
	@Override
	public RequestParameterParser<?> getProcessor(RequestParameter parameter) {
		return processors.get(parameter);
	}
}
