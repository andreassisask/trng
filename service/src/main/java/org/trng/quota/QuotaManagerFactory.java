package org.trng.quota;

import org.trng.service.exception.ServiceFailedException;

public interface QuotaManagerFactory {
	public QuotaManager getInstance(String clazz) throws ServiceFailedException;
}
