package org.trng.service;

import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;

public interface RequestProcessor {
	public ServiceResponse processRequest(ServiceRequest serviceRequest) throws InvalidServiceRequestException,
			ServiceFailedException;
}
