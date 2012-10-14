package org.trng.service.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class InvalidServiceRequestExceptionTest {

	@Test
	public void testInvalidServiceRequestException() {
		InvalidServiceRequestException exception = new InvalidServiceRequestException();
		assertNull(exception.getCause());
		assertNull(exception.getMessage());
	}

	@Test
	public void testInvalidServiceRequestExceptionStringThrowable() {
		Throwable t = new IllegalArgumentException("illegal");
		InvalidServiceRequestException exception = new InvalidServiceRequestException("msg", t);
		assertEquals(t, exception.getCause());
		assertEquals("msg", exception.getMessage());

	}

	@Test
	public void testInvalidServiceRequestExceptionString() {
		InvalidServiceRequestException exception = new InvalidServiceRequestException("msg");
		assertNull(exception.getCause());
		assertEquals("msg", exception.getMessage());
	}

	@Test
	public void testInvalidServiceRequestExceptionThrowable() {
		Throwable t = new IllegalArgumentException("illegal");
		InvalidServiceRequestException exception = new InvalidServiceRequestException(t);
		assertEquals(t, exception.getCause());
		assertEquals("java.lang.IllegalArgumentException: illegal", exception.getMessage());
	}

}
