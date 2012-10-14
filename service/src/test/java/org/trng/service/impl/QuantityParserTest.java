package org.trng.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trng.service.exception.InvalidServiceRequestException;

public class QuantityParserTest {
	private QuantityParser processor;

	@Before
	public void setUp() throws Exception {
		processor = new QuantityParser();
	}

	@Test
	public void testProcessParameterValue() throws InvalidServiceRequestException {
		Integer q = processor.processParameterValue("16");
		assertEquals(new Integer(16), q);
	}

	@Test(expected = InvalidServiceRequestException.class)
	public void testProcessParameterValueNull() throws InvalidServiceRequestException {
		processor.processParameterValue(null);
	}

	@Test(expected = InvalidServiceRequestException.class)
	public void testProcessParameterValueInvalid() throws InvalidServiceRequestException {
		processor.processParameterValue("a");
	}

}
