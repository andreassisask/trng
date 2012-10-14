package org.trng.store.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class DirRandomStoreTest {
	private DirRandomStore store;

	public static final byte[] file1bytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	public static final byte[] file2bytes = new byte[] { 9, 10, 11, 12, 13, 14, 15, 16 };

	public static final String file1name = "file1";
	public static final String file2name = "file2";

	private File file1;
	private File file2;

	private File dir;

	@Before
	public void setUp() throws Exception {
		File tmpDir = FileUtils.getTempDirectory();
		dir = new File(tmpDir, "DirRandomStoreTest");

		FileUtils.deleteQuietly(dir);
		dir.mkdir();

		file1 = new File(dir, file1name);
		file2 = new File(dir, file2name);

		FileUtils.writeByteArrayToFile(file1, file1bytes);
		FileUtils.writeByteArrayToFile(file2, file2bytes);

		store = new DirRandomStore();
		store.setSourceDir(dir);

	}

	@Test
	public void testMakeProcessedDir() {
		store.makeProcessedDir(dir);
		assertTrue(new File(dir, DirRandomStore.PROCESSED_DIR).exists());
	}

	@Test
	public void testDirRandomStore() {
		DirRandomStore drs = new DirRandomStore();
		assertNull(drs.getSourceDir());
	}

	@Test
	public void testGetInputStream() throws IOException {
		InputStream is;
		byte[] bytes;

		is = store.getInputStream(4);
		bytes = IOUtils.toByteArray(is);

		verifyArray(bytes, 0);

		is = store.getInputStream(8);
		bytes = IOUtils.toByteArray(is);

		verifyArray(bytes, 4);

		is = store.getInputStream(4);
		bytes = IOUtils.toByteArray(is);

		verifyArray(bytes, 12);
	}

	protected void verifyArray(byte[] array, int start) {
		for (int i = 0; i < array.length; i++) {
			assertEquals(start + i + 1, array[i]);
		}
	}

	@Test
	public void testSetGetSourceDir() {
		store.setSourceDir(FileUtils.getTempDirectory());
		assertEquals(FileUtils.getTempDirectory(), store.getSourceDir());
	}

	@Test
	public void testSetProperties() {
		Properties p = new Properties();
		p.setProperty(DirRandomStore.SOURCE_DIR_PROPERTY, FileUtils.getTempDirectoryPath());
		store.setProperties(p);
		assertEquals(FileUtils.getTempDirectory(), store.getSourceDir());
	}

}
