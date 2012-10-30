package org.trng.quota.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class PeriodFixedQuotaTest {
	private static final int QUOTA = 8; // 8 bytes
	private static final long PERIOD = 5000L; // per 5 seconds

	private PeriodFixedQuota quota;

	@Before
	public void setUp() throws Exception {
		quota = new PeriodFixedQuota(PERIOD, QUOTA);
	}

	@Test
	public void testDefaults() {
		PeriodFixedQuota q = new PeriodFixedQuota();
		assertEquals(PeriodFixedQuota.DEFAULT_PERIOD, q.getPeriod());
		assertEquals(PeriodFixedQuota.DEFAULT_QUOTA, q.getQuota());
	}

	@Test
	public void testGetPeriod() {
		assertEquals(PERIOD, quota.getPeriod());
	}

	@Test
	public void testSetPeriod() {
		quota.setPeriod(2 * PERIOD);
		assertEquals(2 * PERIOD, quota.getPeriod());
	}

	@Test
	public void testGetQuota() {
		assertEquals(QUOTA, quota.getQuota());
	}

	@Test
	public void testSetQuota() {
		quota.setQuota(2 * QUOTA);
		assertEquals(2 * QUOTA, quota.getQuota());
	}

	@Test
	public void testCheckAvailable() {
		// Set a long period (24h) which is very unlikely to expire
		// before the test completes
		quota.setPeriod(1000L * 60 * 60 * 24);
		quota.removeFromQuota("a", QUOTA - 3);

		assertFalse(quota.checkAvailable("a", 4));
		assertFalse(quota.checkAvailable("a", 3));
		assertTrue(quota.checkAvailable("a", 2));
		assertTrue(quota.checkAvailable("a", 1));

	}

	@Test
	public void testRemoveFromQuotaStringInt() {
		long before = System.currentTimeMillis();
		quota.removeFromQuota("a", 3);
		long after = System.currentTimeMillis();

		Map<String, SortedMap<Long, Integer>> usageMap = quota.getUsageMap();
		SortedMap<Long, Integer> a = usageMap.get("a");

		assertEquals(a.size(), 1);
		Entry<Long, Integer> e = a.entrySet().iterator().next();

		assertEquals(new Integer(3), e.getValue());
		assertTrue(before <= e.getKey());
		assertTrue(after >= e.getKey());
	}

	@Test
	public void testGetAvailableString() {
		Map<String, SortedMap<Long, Integer>> usageMap = quota.getUsageMap();

		SortedMap<Long, Integer> a = new TreeMap<Long, Integer>();
		usageMap.put("a", a);

		long before = System.currentTimeMillis();

		// Expired
		a.put(before - PERIOD, 1);

		// Valid, unless execution of this test takes more than 1 day
		a.put(before + 1000L * 60 * 60 * 24, 3);

		int q = quota.getAvailable("a");
		assertEquals(QUOTA - 3, q);
	}

	@Test
	public void testRemoveFromQuotaStringIntLong() {
		long now = System.currentTimeMillis();
		Map<String, SortedMap<Long, Integer>> usageMap = quota.getUsageMap();

		quota.removeFromQuota("a", 2, now);
		quota.removeFromQuota("b", 3, now);

		SortedMap<Long, Integer> aMap = usageMap.get("a");
		SortedMap<Long, Integer> bMap = usageMap.get("b");

		assertEquals(1, aMap.size());
		assertEquals(1, bMap.size());
		assertEquals(new Integer(2), aMap.get(now));
		assertEquals(new Integer(3), bMap.get(now));

		quota.removeFromQuota("a", 5, now + PERIOD / 2);
		quota.removeFromQuota("b", 8, now + PERIOD);

		aMap = usageMap.get("a");
		bMap = usageMap.get("b");

		assertEquals(2, aMap.size());
		assertEquals(1, bMap.size());
		assertEquals(new Integer(2), aMap.get(now));
		assertEquals(new Integer(5), aMap.get(now + PERIOD / 2));
		assertEquals(new Integer(8), bMap.get(now + PERIOD));

	}

	@Test
	public void testGetAvailableStringLong() {
		long now = System.currentTimeMillis();
		Map<String, SortedMap<Long, Integer>> usageMap = quota.getUsageMap();

		// Nothing in usageMap
		assertEquals(QUOTA, quota.getAvailable("a", now));
		assertEquals(QUOTA, quota.getAvailable("b", now));

		SortedMap<Long, Integer> aMap = new TreeMap<Long, Integer>();
		SortedMap<Long, Integer> bMap = new TreeMap<Long, Integer>();

		// Empty usage in usageMap
		usageMap.put("a", aMap);
		usageMap.put("b", bMap);

		assertEquals(QUOTA, quota.getAvailable("a", now));
		assertEquals(QUOTA, quota.getAvailable("b", now));

		// Active usage for a, old for b
		aMap.put(now - PERIOD + PERIOD / 2, 2);
		aMap.put(now, 2);
		bMap.put(now - PERIOD, 2);
		bMap.put(now - 2 * PERIOD, 2);

		assertEquals(QUOTA - 4, quota.getAvailable("a", now));
		assertEquals(QUOTA, quota.getAvailable("b", now));

	}

	@Test
	public void testGetUsageAfter() {
		SortedMap<Long, Integer> usageData = new TreeMap<Long, Integer>();
		long now = System.currentTimeMillis();

		assertEquals(0, quota.getUsageAfter(usageData, now - 1000L));
		assertEquals(0, quota.getUsageAfter(usageData, now));

		usageData.put(now, 1);
		usageData.put(now + 5L, 1);
		usageData.put(now + 10L, 1);
		usageData.put(now + 100L, 1);

		assertEquals(4, quota.getUsageAfter(usageData, now - 1L));
		assertEquals(3, quota.getUsageAfter(usageData, now));
		assertEquals(3, quota.getUsageAfter(usageData, now + 4L));
		assertEquals(2, quota.getUsageAfter(usageData, now + 5L));
		assertEquals(1, quota.getUsageAfter(usageData, now + 99L));
		assertEquals(0, quota.getUsageAfter(usageData, now + 100L));
		assertEquals(0, quota.getUsageAfter(usageData, now + 1000L));

	}
}
