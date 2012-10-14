package org.trng.format.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trng.format.RandomFormat;

public class DefaultRandomFormatterFactoryTest {
	private DefaultRandomFormatterFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new DefaultRandomFormatterFactory();
	}

	@Test
	public void testGetFormatter() {
		assertEquals(RawFormatter.class, factory.getFormatter(RandomFormat.RAW).getClass());
		assertEquals(AsciiFormatter.class, factory.getFormatter(RandomFormat.ASCII).getClass());
		assertEquals(HexFormatter.class, factory.getFormatter(RandomFormat.HEX).getClass());
	}
}
