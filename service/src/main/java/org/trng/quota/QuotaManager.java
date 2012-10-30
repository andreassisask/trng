package org.trng.quota;

public interface QuotaManager {

	public boolean checkAvailable(String id, int bytes);

	public void removeFromQuota(String id, int bytes);

	public int getAvailable(String id);
}
