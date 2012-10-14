package org.trng.store.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DirInputStreamTest {
	public static final byte[] file1bytes = new byte[] { 1, 2 };
	public static final byte[] file2bytes = new byte[] {};
	public static final byte[] file3bytes = new byte[] { 3, 4 };

	public static final String file1name = "file1";
	public static final String file2name = "file2";
	public static final String file3name = "file3";

	private File file1;
	private File file2;
	private File file3;

	private File sourceDir;
	private File processedDir;

	private DirInputStream dis;

	@After
	public void tearDown() throws Exception {
		dis.close();
	}

	@Before
	public void setUp() throws Exception {
		File tmpDir = FileUtils.getTempDirectory();

		sourceDir = new File(tmpDir, "DirInputStreamTestSource");
		processedDir = new File(tmpDir, "DirInputStreamTestProcessed");

		FileUtils.deleteDirectory(sourceDir);
		sourceDir.mkdir();

		FileUtils.deleteDirectory(processedDir);
		processedDir.mkdir();

		file1 = new File(sourceDir, file1name);
		file2 = new File(sourceDir, file2name);
		file3 = new File(sourceDir, file3name);

		FileUtils.writeByteArrayToFile(file1, file1bytes);
		FileUtils.writeByteArrayToFile(file2, file2bytes);
		FileUtils.writeByteArrayToFile(file3, file3bytes);

		dis = new DirInputStream(sourceDir, processedDir);
	}

	@Test
	public void testRead() throws IOException {
		// No request made yet, must return -1 at once
		assertEquals(-1, dis.read());

		// Request 1 byte
		dis.newRequest(1);
		assertEquals(1, dis.read());
		assertEquals(-1, dis.read());

		// Request 2 bytes
		dis.newRequest(2);
		assertEquals(2, dis.read());
		assertEquals(3, dis.read());
		assertEquals(-1, dis.read());

		// Request another 2 bytes, must fail after first as we're out of data
		dis.newRequest(2);
		assertEquals(4, dis.read());
		try {
			dis.read();
			fail();
		} catch (Throwable t) {
			assertEquals(IOException.class, t.getClass());
		}
	}

	@Test
	public void testDirInputStream() {
		assertEquals(0, dis.getBytesRead());
		assertEquals(0, dis.getBytesRequired());
		assertEquals(sourceDir, dis.getSourceDir());
		assertEquals(processedDir, dis.getProcessedDir());
		assertNull(dis.getCurrentStream());

	}

	@Test
	public void testNewRequest() throws IOException {
		dis.newRequest(5);
		assertEquals(5, dis.getBytesRequired());
		assertEquals(0, dis.getBytesRead());
		dis.read();
		assertEquals(5, dis.getBytesRequired());
		assertEquals(1, dis.getBytesRead());
		dis.newRequest(6);
		assertEquals(6, dis.getBytesRequired());
		assertEquals(0, dis.getBytesRead());
	}

	@Test
	public void testGetRemaining() throws IOException {
		dis.newRequest(8);
		assertEquals(8, dis.getRemaining());
		dis.read();
		assertEquals(7, dis.getRemaining());
	}

	@Test
	public void testEnsureInputStream() throws IOException {
		InputStream is1 = dis.ensureInputStream(null, 0, sourceDir,
				processedDir);
		assertNotNull(is1);
		assertEquals(1, is1.read());
		is1.close();

		InputStream is2 = dis
				.ensureInputStream(is1, 0, sourceDir, processedDir);
		assertTrue(is1 == is2);

		InputStream is3 = dis.ensureInputStream(is2, -1, sourceDir,
				processedDir);
		assertFalse(is2 == is3);
		assertEquals(-1, is3.read());// file 2 is empty
		is3.close();

	}

	@Test
	public void testGetNextFile() throws IOException {
		File f;

		f = dis.getNextFile(sourceDir, processedDir);
		assertEquals(new File(processedDir, file1name), f);

		f = dis.getNextFile(sourceDir, processedDir);
		assertEquals(new File(processedDir, file2name), f);

		f = dis.getNextFile(sourceDir, processedDir);
		assertEquals(new File(processedDir, file3name), f);

		try {
			dis.getNextFile(sourceDir, processedDir);
			fail();
		} catch (Throwable t) {
			assertEquals(IOException.class, t.getClass());
		}
	}

	@Test
	public void testGetFileList() {
		List<File> files = dis.getFileList(sourceDir);
		assertEquals(3, files.size());
		assertEquals(file1, files.get(0));
		assertEquals(file2, files.get(1));
		assertEquals(file3, files.get(2));

	}
}
