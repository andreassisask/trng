package org.trng.store.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class DummyRandomStoreTest {
	private DummyRandomStore store;

	@Before
	public void setUp() throws Exception {
		store = new DummyRandomStore();
	}

	@Test
	public void testGetInputStream() throws IOException {
		InputStream is;
		ByteArrayOutputStream os;
		byte[] b;

		os = new ByteArrayOutputStream();
		is = store.getInputStream(0);
		IOUtils.copy(is, os);
		b = os.toByteArray();
		assertEquals(0, b.length);

		os = new ByteArrayOutputStream();
		is = store.getInputStream(1);
		IOUtils.copy(is, os);
		b = os.toByteArray();
		assertEquals(1, b.length);
		assertEquals(0, b[0]);

		os = new ByteArrayOutputStream();
		is = store.getInputStream(2);
		IOUtils.copy(is, os);
		b = os.toByteArray();
		assertEquals(2, b.length);
		assertEquals(0, b[0]);
		assertEquals(1, b[1]);

		os = new ByteArrayOutputStream();
		is = store.getInputStream(3);
		IOUtils.copy(is, os);
		b = os.toByteArray();
		assertEquals(3, b.length);
		assertEquals(0, b[0]);
		assertEquals(1, b[1]);
		assertEquals(0, b[2]);
	}

	@Test
	public void testGetAvailableProperties() {
		String[] s = store.getAvailableProperties();
		assertEquals(0, s.length);
	}

	@Test
	public void testSetProperties() {
		store.setProperties(null);
	}

}
