package org.trng.service.impl;

import org.trng.format.RandomFormat;
import org.trng.service.RequestParameterParser;
import org.trng.service.exception.InvalidServiceRequestException;

public class FormatParser implements RequestParameterParser<RandomFormat> {

	@Override
	public RandomFormat processParameterValue(String paramValue) throws InvalidServiceRequestException {
		RandomFormat f = RandomFormat.get(paramValue);
		if (f == null)
			throw new InvalidServiceRequestException("Invalid format: " + paramValue);

		return f;
	}

}
