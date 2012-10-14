package org.trng.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trng.format.RandomFormatterFactory;
import org.trng.format.impl.DefaultRandomFormatterFactory;
import org.trng.service.RequestProcessor;
import org.trng.service.RequestProcessorFactory;
import org.trng.service.exception.ServiceFailedException;
import org.trng.store.RandomStore;

public class DefaultRequestProcessorFactory implements RequestProcessorFactory {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRequestProcessorFactory.class);

	private Map<String, RequestProcessor> processors = new HashMap<String, RequestProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.trng.service.impl.RequestProcessorFactory#getInstance(java.lang.String
	 * )
	 */
	@Override
	public RequestProcessor getInstance(String randomStoreClass, Properties properties) throws ServiceFailedException {
		// File d = getStoreDir(storeDir);
		RequestProcessor p = processors.get(randomStoreClass);

		if (p == null) {
			RandomStore r = createStore(randomStoreClass, properties);
			p = createProcessor(r);
			processors.put(randomStoreClass, p);
		}

		return p;
	}

	protected RequestProcessor createProcessor(RandomStore randomStore) throws ServiceFailedException {
		return new DefaultRequestProcessor(randomStore, createFormatterFactory());
	}

	protected RandomFormatterFactory createFormatterFactory() {
		return new DefaultRandomFormatterFactory();
	}

	protected RandomStore createStore(String randomStoreClass, Properties properties) throws ServiceFailedException {
		RandomStore randomStore = null;

		try {
			@SuppressWarnings("unchecked")
			Class<RandomStore> clazz = (Class<RandomStore>) Class.forName(randomStoreClass);
			randomStore = clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new ServiceFailedException(e);
		}

		randomStore.setProperties(properties);
		return randomStore;
	}

	protected File getStoreDir(String storeDir) throws ServiceFailedException {
		if (storeDir == null || storeDir.isEmpty()) {
			String message = "The random store directory is incorrect: " + storeDir;
			LOG.warn(message);
			throw new ServiceFailedException(message);
		} else {
			LOG.info(String.format("Random store is set to folder '%s'", storeDir));
			return new File(storeDir);
		}
	}
}
