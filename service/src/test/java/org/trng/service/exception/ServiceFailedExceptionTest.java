package org.trng.service.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ServiceFailedExceptionTest {

	@Test
	public void testServiceFailedException() {
		ServiceFailedException exception = new ServiceFailedException();
		assertNull(exception.getCause());
		assertNull(exception.getMessage());

	}

	@Test
	public void testServiceFailedExceptionStringThrowable() {
		Throwable t = new IllegalArgumentException("illegal");
		ServiceFailedException exception = new ServiceFailedException("msg", t);
		assertEquals(t, exception.getCause());
		assertEquals("msg", exception.getMessage());
	}

	@Test
	public void testServiceFailedExceptionString() {
		ServiceFailedException exception = new ServiceFailedException("msg");
		assertNull(exception.getCause());
		assertEquals("msg", exception.getMessage());
	}

	@Test
	public void testServiceFailedExceptionThrowable() {
		Throwable t = new IllegalArgumentException("illegal");
		ServiceFailedException exception = new ServiceFailedException(t);
		assertEquals(t, exception.getCause());
		assertEquals("java.lang.IllegalArgumentException: illegal", exception.getMessage());
	}

}
