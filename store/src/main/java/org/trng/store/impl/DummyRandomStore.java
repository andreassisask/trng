package org.trng.store.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.trng.store.RandomStore;

public class DummyRandomStore implements RandomStore {
	private Properties properties;

	public InputStream getInputStream(int bytes) throws IOException {
		byte[] b = new byte[bytes];
		for (int i = 0; i < bytes; i++) {
			b[i] = (byte) ((byte) i % 2);
		}
		return new ByteArrayInputStream(b);
	}

	public String[] getAvailableProperties() {
		return new String[] {};
	}

	public void setProperties(Properties p) {
		this.properties = p;
	}

	public Properties getProperties() {
		return properties;
	}

}
