package org.trng.quota.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.trng.quota.QuotaManager;
import org.trng.service.exception.ServiceFailedException;

public class DefaultQuotaManagerFactoryTest {
	private DefaultQuotaManagerFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new DefaultQuotaManagerFactory();
	}

	@Test
	public void testGetInstance() throws ServiceFailedException {
		QuotaManager m1 = factory.getInstance("org.trng.quota.impl.PeriodFixedQuota");
		QuotaManager m2 = factory.getInstance("org.trng.quota.impl.PeriodFixedQuota");

		assertNotNull(m1);
		assertNotNull(m2);

		assertSame(m1, m2);

	}

	@Test
	public void testCreateInstance() throws ServiceFailedException {
		QuotaManager m1 = factory.createInstance("org.trng.quota.impl.PeriodFixedQuota");
		QuotaManager m2 = factory.createInstance("org.trng.quota.impl.PeriodFixedQuota");

		assertNotNull(m1);
		assertNotNull(m2);

		assertNotSame(m1, m2);

	}

	@Test
	public void testCreateInstanceInvalidClass() throws ServiceFailedException {
		try {
			factory.createInstance("org.trng.quota.impl.NonExistantQuota");
			fail();
		} catch (Throwable t) {
			assertEquals(ServiceFailedException.class, t.getClass());
			assertEquals(ClassNotFoundException.class, t.getCause().getClass());
		}
	}

}
