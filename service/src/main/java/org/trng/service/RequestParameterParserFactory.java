package org.trng.service;

public interface RequestParameterParserFactory {

	public RequestParameterParser<?> getProcessor(RequestParameter parameter);

}