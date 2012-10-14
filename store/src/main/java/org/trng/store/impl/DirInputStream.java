package org.trng.store.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DirInputStream extends InputStream {
	private InputStream currentStream;
	// private File currentFile;

	private File sourceDir;
	private File processedDir;

	private int bytesRequired;
	private int bytesRead;

	public DirInputStream(File sourceDir, File processedDir) {
		this.sourceDir = sourceDir;
		this.processedDir = processedDir;
		this.bytesRead = 0;
		this.bytesRequired = 0;
	}

	protected InputStream getCurrentStream() {
		return currentStream;
	}

	protected File getSourceDir() {
		return sourceDir;
	}

	protected File getProcessedDir() {
		return processedDir;
	}

	protected int getBytesRequired() {
		return bytesRequired;
	}

	protected int getBytesRead() {
		return bytesRead;
	}

	public void newRequest(int bytesRequired) {
		this.bytesRequired = bytesRequired;
		this.bytesRead = 0;
	}

	@Override
	public void close() throws IOException {
		if (currentStream != null) {
			currentStream.close();
		}
	}

	@Override
	public int read() throws IOException {
		int remaining = getRemaining();
		if (remaining <= 0) {
			return -1;
		}

		int read = 0;
		currentStream = ensureInputStream(currentStream, read, sourceDir,
				processedDir);
		read = currentStream.read();

		// If nothing was read, try from next file until read or out of files
		while (read == -1) {
			currentStream = ensureInputStream(currentStream, read, sourceDir,
					processedDir);
			read = currentStream.read();
		}

		bytesRead++;
		return read;
	}

	// @Override
	// public int read(byte b[], int off, int len) throws IOException {
	// int remaining = getRemaining();
	// if (remaining <= 0) {
	// return -1;
	// }
	//
	// int read = 0;
	// ensureInputStream(read);
	// read = currentStream.read(b, off, Math.min(len, remaining));
	//
	// // If nothing was read, try from next file until read or out of files
	// while (read == -1) {
	// ensureInputStream(read);
	// read = currentStream.read(b, off, Math.min(len, remaining));
	// }
	//
	// return read;
	// }
	//
	// @Override
	// public int read(byte b[]) throws IOException {
	// return read(b, 0, b.length);
	// }

	protected int getRemaining() {
		return bytesRequired - bytesRead;
	}

	protected InputStream ensureInputStream(InputStream currentStream,
			int lastRead, File sourceDir, File processedDir) throws IOException {
		if (currentStream != null && lastRead != -1)
			return currentStream;

		if (currentStream != null)
			currentStream.close();

		File newFile = getNextFile(sourceDir, processedDir);
		return new BufferedInputStream(new FileInputStream(newFile));
	}

	protected File getNextFile(File dir, File processedDir) throws IOException {
		List<File> files = getFileList(dir);

		if (files.isEmpty())
			throw new IOException("Out of data");

		File f = files.get(0);
		File movedFile = new File(processedDir, f.getName());
		FileUtils.moveFile(f, movedFile);

		return movedFile;
	}

	protected List<File> getFileList(File sourceDir) {
		List<File> files = Collections.synchronizedList(new ArrayList<File>(
				FileUtils.listFiles(sourceDir, null, false)));

		Collections.sort(files, new Comparator<File>() {

			public int compare(File o1, File o2) {
				return o1.compareTo(o2);
			}
		});
		return files;
	}
}
