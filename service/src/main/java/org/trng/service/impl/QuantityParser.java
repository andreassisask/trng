package org.trng.service.impl;

import org.trng.service.RequestParameterParser;
import org.trng.service.exception.InvalidServiceRequestException;

public class QuantityParser implements RequestParameterParser<Integer> {

	@Override
	public Integer processParameterValue(String paramValue) throws InvalidServiceRequestException {
		int quantity = 0;

		try {
			quantity = Integer.parseInt(paramValue);
		} catch (NumberFormatException e) {
			throw new InvalidServiceRequestException("Invalid quantity: " + paramValue);
		}

		return quantity;
	}
}
