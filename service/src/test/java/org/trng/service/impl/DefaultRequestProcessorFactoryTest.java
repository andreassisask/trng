package org.trng.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.trng.format.RandomFormatterFactory;
import org.trng.service.RequestProcessor;
import org.trng.service.exception.ServiceFailedException;
import org.trng.store.RandomStore;
import org.trng.store.impl.DummyRandomStore;

public class DefaultRequestProcessorFactoryTest {
	private DefaultRequestProcessorFactory factory;

	@Before
	public void setUp() {
		factory = new DefaultRequestProcessorFactory();
	}

	@Test
	public void testGetInstance() throws ServiceFailedException {
		RequestProcessor rp1 = factory.getInstance("org.trng.store.impl.DummyRandomStore", null);
		assertNotNull(rp1);

		RequestProcessor rp2 = factory.getInstance("org.trng.store.impl.DummyRandomStore", null);
		assertNotNull(rp2);

		assertSame(rp1, rp2);
	}

	@Test
	public void testCreateProcessor() throws ServiceFailedException {
		RequestProcessor rp = factory.createProcessor(new DummyRandomStore());
		assertNotNull(rp);
	}

	@Test
	public void testCreateFormatterFactory() {
		RandomFormatterFactory rff = factory.createFormatterFactory();
		assertNotNull(rff);
	}

	@Test
	public void testCreateStore() throws ServiceFailedException {
		Properties p = new Properties();
		RandomStore rs1 = factory.createStore("org.trng.store.impl.DummyRandomStore", p);

		assertEquals(DummyRandomStore.class, rs1.getClass());

		DummyRandomStore drs = (DummyRandomStore) rs1;
		assertSame(p, drs.getProperties());
	}

	@Test(expected = ServiceFailedException.class)
	public void testGetStoreDirNull() throws ServiceFailedException {
		factory.getStoreDir(null);
	}

	@Test(expected = ServiceFailedException.class)
	public void testGetStoreDirEmpty() throws ServiceFailedException {
		factory.getStoreDir("");
	}

	@Test
	public void testGetStoreDir() throws ServiceFailedException {
		String d = FileUtils.getTempDirectoryPath();
		File f = factory.getStoreDir(d);
		assertEquals(new File(d), f);
	}

}
