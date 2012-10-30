package org.trng.quota.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.trng.quota.QuotaManager;

/**
 * A quota manager which will assume each user (identified using a string which
 * can be a IP address, user name, etc.) has a fixed number of bytes for fixed
 * period.
 */
public class PeriodFixedQuota implements QuotaManager {
	public static final int DEFAULT_QUOTA = 1024 * 1024; // 1MB
	public static final long DEFAULT_PERIOD = 1000L * 60 * 60 * 24; // per 24h

	private long period;
	private int quota;

	// Contains the usage data - for each user identified by String there is a
	// SortedMap where key is the absolute time
	// of usage in millis and value is the number of bytes
	private Map<String, SortedMap<Long, Integer>> usageMap;

	public PeriodFixedQuota() {
		this(DEFAULT_PERIOD, DEFAULT_QUOTA);
	}

	public PeriodFixedQuota(long period, int quota) {
		this.period = period;
		this.quota = quota;
		this.usageMap = new HashMap<String, SortedMap<Long, Integer>>();
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	protected Map<String, SortedMap<Long, Integer>> getUsageMap() {
		return usageMap;
	}

	@Override
	public boolean checkAvailable(String id, int bytes) {
		int available = getAvailable(id);
		return available > bytes;
	}

	@Override
	public void removeFromQuota(String id, int bytes) {
		removeFromQuota(id, bytes, System.currentTimeMillis());
	}

	@Override
	public int getAvailable(String id) {
		return getAvailable(id, System.currentTimeMillis());
	}

	protected void removeFromQuota(String id, int bytes, long asOf) {
		SortedMap<Long, Integer> usageData = usageMap.get(id);

		if (usageData == null) {
			usageData = new TreeMap<Long, Integer>();
		} else {
			// Do cleanup by keeping only the records in the "last" period
			usageData = usageData.tailMap(asOf - period + 1);
		}

		// Add new record
		usageData.put(asOf, Math.abs(bytes));

		// Put back to the main map
		usageMap.put(id, usageData);
	}

	protected int getAvailable(String id, long asOf) {
		SortedMap<Long, Integer> usageData = usageMap.get(id);
		if (usageData == null || usageData.isEmpty()) {
			return quota;
		}

		int usage = getUsageAfter(usageData, asOf - period);
		return quota - usage;
	}

	protected int getUsageAfter(SortedMap<Long, Integer> usageData, long after) {
		SortedMap<Long, Integer> usageAfter = usageData.tailMap(after + 1);
		int total = 0;
		for (int i : usageAfter.values()) {
			total += i;
		}
		return total;
	}

}
