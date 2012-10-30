package org.trng.quota.impl;

import java.util.HashMap;
import java.util.Map;

import org.trng.quota.QuotaManager;
import org.trng.quota.QuotaManagerFactory;
import org.trng.service.exception.ServiceFailedException;

public class DefaultQuotaManagerFactory implements QuotaManagerFactory {
	private Map<String, QuotaManager> managers;

	public DefaultQuotaManagerFactory() {
		managers = new HashMap<String, QuotaManager>();
	}

	@Override
	public QuotaManager getInstance(String clazz) throws ServiceFailedException {
		QuotaManager qm = managers.get(clazz);
		if (qm == null) {
			qm = createInstance(clazz);
			managers.put(clazz, qm);
		}
		return qm;
	}

	public QuotaManager createInstance(String clazz) throws ServiceFailedException {
		try {
			@SuppressWarnings("unchecked")
			Class<QuotaManager> c = (Class<QuotaManager>) Class.forName(clazz);
			return c.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new ServiceFailedException("Failed to initialize quota manager", e);
		}
	}

}
