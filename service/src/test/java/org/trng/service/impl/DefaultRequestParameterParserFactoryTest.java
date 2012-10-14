package org.trng.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trng.service.RequestParameter;

public class DefaultRequestParameterParserFactoryTest {
	private DefaultRequestParameterParserFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new DefaultRequestParameterParserFactory();
	}

	@Test
	public void testGetProcessor() {
		assertEquals(FormatParser.class, factory.getProcessor(RequestParameter.FORMAT).getClass());
		assertEquals(QuantityParser.class, factory.getProcessor(RequestParameter.QUANTITY).getClass());
	}
}
