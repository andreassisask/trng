package org.trng.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.trng.format.RandomFormat;
import org.trng.format.RandomFormatter;
import org.trng.format.RandomFormatterFactory;
import org.trng.service.RequestProcessor;
import org.trng.service.ServiceRequest;
import org.trng.service.ServiceResponse;
import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;
import org.trng.store.RandomStore;

public class DefaultRequestProcessor implements RequestProcessor {
	private RandomStore randomStore;
	private RandomFormatterFactory formatterFactory;

	public DefaultRequestProcessor(RandomStore randomStore, RandomFormatterFactory formatterFactory) {
		this.randomStore = randomStore;
		this.formatterFactory = formatterFactory;
	}

	protected RandomStore getRandomStore() {
		return randomStore;
	}

	protected RandomFormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	@Override
	public ServiceResponse processRequest(ServiceRequest serviceRequest) throws InvalidServiceRequestException,
			ServiceFailedException {
		if (serviceRequest == null)
			throw new IllegalArgumentException("Service request is null");

		serviceRequest.validate();

		// At the moment we only support until integer max value
		// long quantity = serviceRequest.getQuantity();
		// if (quantity > Integer.MAX_VALUE) {
		// throw new
		// ServiceFailedException("Unfortunately currently quantity is limited to Integer.MAX_VALUE bytes");
		// }

		ServiceResponse response = new ServiceResponse();

		try {
			InputStream is = randomStore.getInputStream(serviceRequest.getQuantity());
			RandomFormat format = serviceRequest.getFormat();
			RandomFormatter formatter = formatterFactory.getFormatter(format);

			if (formatter.isBinary()) {
				formatter.formatBinary(is, serviceRequest.getOutputStream());
			} else {
				formatter.formatText(is, serviceRequest.getWriter());
			}

			response.setRandomFormatter(formatter);

		} catch (IOException e) {
			throw new ServiceFailedException(e);
		}

		return response;
	}
}
