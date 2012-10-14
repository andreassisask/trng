package org.trng.format.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class AsciiFormatterTest {
	private AsciiFormatter asciiFormatter;
	private Writer writer;

	@Before
	public void setUp() throws Exception {
		asciiFormatter = new AsciiFormatter();
		writer = Mockito.mock(Writer.class);

	}

	@Test
	public void testFormatText() throws IOException {
		byte[] a = new byte[] { 1, 2, 3, 4 };
		ByteArrayInputStream is = new ByteArrayInputStream(a);

		InOrder io = Mockito.inOrder(writer);
		asciiFormatter.formatText(is, writer);

		io.verify(writer).write("00000001");
		io.verify(writer).write("00000010");
		io.verify(writer).write("00000011");
		io.verify(writer).write("00000100");

		io.verifyNoMoreInteractions();
	}

	@Test
	public void testFormatByteArray() throws IOException {
		byte[] a = new byte[] { 1, 2, 3 };

		InOrder io = Mockito.inOrder(writer);
		asciiFormatter.formatByteArray(a, 2, writer);
		io.verify(writer).write("00000001");
		io.verify(writer).write("00000010");
		io.verifyNoMoreInteractions();

	}

	@Test
	public void testIsBinary() {
		assertFalse(asciiFormatter.isBinary());
	}

	@Test
	public void testFormatBinary() {
		try {
			asciiFormatter.formatBinary(null, null);
			fail();
		} catch (Throwable t) {
			assertEquals(UnsupportedOperationException.class, t.getClass());
		}
	}

	@Test
	public void testGetContentType() throws Exception {
		assertEquals("text/plain", asciiFormatter.getContentType());
	}

}
