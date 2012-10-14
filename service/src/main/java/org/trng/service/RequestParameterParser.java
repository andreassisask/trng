package org.trng.service;

import org.trng.service.exception.InvalidServiceRequestException;

public interface RequestParameterParser<T> {
	T processParameterValue(String paramValue) throws InvalidServiceRequestException;
}
