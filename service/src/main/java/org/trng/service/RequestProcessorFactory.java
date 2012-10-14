package org.trng.service;

import java.util.Properties;

import org.trng.service.exception.ServiceFailedException;

public interface RequestProcessorFactory {

	public RequestProcessor getInstance(String randomStoreClass, Properties properties) throws ServiceFailedException;

}