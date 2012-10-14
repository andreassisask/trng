package org.trng.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.trng.format.RandomFormat;
import org.trng.service.exception.InvalidServiceRequestException;

public class FormatParserTest {
	private FormatParser processor;

	@Before
	public void setUp() throws Exception {
		processor = new FormatParser();
	}

	@Test
	public void testProcessParameterValue() throws InvalidServiceRequestException {
		RandomFormat f = processor.processParameterValue("raw");
		assertEquals(RandomFormat.RAW, f);
	}

	@Test(expected = InvalidServiceRequestException.class)
	public void testProcessParameterValueNull() throws InvalidServiceRequestException {
		processor.processParameterValue(null);
	}

	@Test(expected = InvalidServiceRequestException.class)
	public void testProcessParameterValueInvalid() throws InvalidServiceRequestException {
		processor.processParameterValue("invalid");
	}

}
