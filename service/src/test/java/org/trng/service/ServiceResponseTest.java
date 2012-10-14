package org.trng.service;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trng.format.RandomFormatter;
import org.trng.format.impl.AsciiFormatter;

@RunWith(MockitoJUnitRunner.class)
public class ServiceResponseTest {

	@Mock
	private RandomFormatter randomFormatter;
	private ServiceResponse serviceResponse;

	@Before
	public void setUp() throws Exception {
		serviceResponse = new ServiceResponse();
		serviceResponse.setRandomFormatter(randomFormatter);
	}

	@Test
	public void testGetRandomFormatter() {
		assertSame(randomFormatter, serviceResponse.getRandomFormatter());
	}

	@Test
	public void testSetRandomFormatter() {
		AsciiFormatter f = new AsciiFormatter();
		serviceResponse.setRandomFormatter(f);
		assertSame(f, serviceResponse.getRandomFormatter());
	}

}
