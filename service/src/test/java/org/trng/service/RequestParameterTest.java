package org.trng.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

public class RequestParameterTest {

	@Test
	public void testGetName() {
		assertEquals("quantity", RequestParameter.QUANTITY.getName());
		assertEquals("format", RequestParameter.FORMAT.getName());
	}

	@Test
	public void testGet() {
		assertEquals(RequestParameter.QUANTITY, RequestParameter.get("quantity"));
		assertEquals(RequestParameter.FORMAT, RequestParameter.get("format"));
	}

	@Test
	public void testGetAll() {
		Collection<RequestParameter> all = RequestParameter.getAll();
		assertEquals(2, all.size());
		assertTrue(all.contains(RequestParameter.QUANTITY));
		assertTrue(all.contains(RequestParameter.FORMAT));
	}

}
