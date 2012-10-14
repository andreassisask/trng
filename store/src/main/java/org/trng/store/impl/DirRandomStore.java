package org.trng.store.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.trng.store.RandomStore;

public class DirRandomStore implements RandomStore {
	public static final String SOURCE_DIR_PROPERTY = "sourceDir";
	public static final String PROCESSED_DIR = ".processed";

	private File sourceDir;
	private DirInputStream dirInputStream;

	public DirRandomStore() {
	}

	public File getSourceDir() {
		return sourceDir;
	}

	protected void setSourceDir(File sourceDir) {
		this.sourceDir = sourceDir;
	}

	protected File makeProcessedDir(File sourceDir) {
		File processedDir = new File(sourceDir, PROCESSED_DIR);
		if (!processedDir.exists()) {
			processedDir.mkdir();
		}
		return processedDir;
	}

	public InputStream getInputStream(int bytes) throws IOException {
		if (dirInputStream == null) {
			dirInputStream = new DirInputStream(sourceDir, makeProcessedDir(sourceDir));
		}
		dirInputStream.newRequest(bytes);
		return dirInputStream;
	}

	public String[] getAvailableProperties() {
		return new String[] { SOURCE_DIR_PROPERTY };
	}

	public void setProperties(Properties p) {
		sourceDir = new File(p.getProperty(SOURCE_DIR_PROPERTY));
	}

}
