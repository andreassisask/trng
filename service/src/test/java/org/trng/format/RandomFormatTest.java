package org.trng.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

public class RandomFormatTest {

	@Test
	public void testGetName() {
		assertEquals("raw", RandomFormat.RAW.getName());
		assertEquals("ascii", RandomFormat.ASCII.getName());
		assertEquals("hex", RandomFormat.HEX.getName());
	}

	@Test
	public void testGet() {
		assertEquals(RandomFormat.RAW, RandomFormat.get("raw"));
		assertEquals(RandomFormat.ASCII, RandomFormat.get("ascii"));
		assertEquals(RandomFormat.HEX, RandomFormat.get("hex"));
	}

	@Test
	public void testGetAllFormats() {
		Collection<RandomFormat> all = RandomFormat.getAllFormats();
		assertEquals(3, all.size());
		assertTrue(all.contains(RandomFormat.RAW));
		assertTrue(all.contains(RandomFormat.ASCII));
		assertTrue(all.contains(RandomFormat.HEX));
	}

}
