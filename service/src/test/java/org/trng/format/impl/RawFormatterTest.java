package org.trng.format.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class RawFormatterTest {
	private RawFormatter formatter;
	private byte[] random = new byte[] { 0, 1, 2, 3 };

	@Before
	public void setUp() throws Exception {
		formatter = new RawFormatter();
	}

	@Test
	public void testFormatText() {
		try {
			formatter.formatText(null, null);
			fail();
		} catch (Throwable t) {
			assertEquals(UnsupportedOperationException.class, t.getClass());
		}
	}

	@Test
	public void testFormatBinary() throws IOException {
		InputStream is = new ByteArrayInputStream(random);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		formatter.formatBinary(is, os);
		assertTrue(Arrays.equals(random, os.toByteArray()));
	}

	@Test
	public void testIsBinary() {
		assertTrue(formatter.isBinary());
	}

	@Test
	public void testGetContentType() throws Exception {
		assertEquals("application/octet-stream", formatter.getContentType());
	}

}
