package org.trng.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface RandomStore {
	public InputStream getInputStream(int bytes) throws IOException;

	public String[] getAvailableProperties();

	public void setProperties(Properties p);
}
