package org.trng.format.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Formatter;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class HexFormatterTest {
	private HexFormatter formatter;
	private byte[] data = new byte[] { 0, 4, 8, 16 };

	@Before
	public void setUp() throws Exception {
		formatter = new HexFormatter();
	}

	@Test
	public void testFormatText() throws IOException {
		InputStream is = new ByteArrayInputStream(data);
		Writer w = new StringWriter();
		formatter.formatText(is, w);

		assertEquals("00040810", w.toString());
	}

	@Test
	public void testToHex() {
		StringBuffer sb = new StringBuffer();
		Formatter f = new Formatter(sb, Locale.ENGLISH);
		formatter.toHex(data, 3, f);

		assertEquals("000408", sb.toString());
	}

	@Test
	public void testIsBinary() {
		assertFalse(formatter.isBinary());
	}

	@Test
	public void testFormatBinary() {
		try {
			formatter.formatBinary(null, null);
			fail();
		} catch (Throwable t) {
			assertEquals(UnsupportedOperationException.class, t.getClass());
		}
	}

	@Test
	public void testGetContentType() throws Exception {
		assertEquals("text/plain", formatter.getContentType());
	}

}
