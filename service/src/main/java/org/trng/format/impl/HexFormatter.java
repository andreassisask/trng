package org.trng.format.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Formatter;

import org.trng.format.RandomFormatter;

public class HexFormatter implements RandomFormatter {
	private static final int BUFFER_SIZE = 1024;

	@Override
	public void formatText(InputStream is, Writer w) throws IOException {
		final Formatter formatter = new Formatter(w);
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = is.read(buffer)) >= 0) {
			toHex(buffer, read, formatter);
		}
	}

	protected void toHex(byte[] bytes, int length, Formatter formatter) {
		for (int i = 0; i < length; i++) {
			formatter.format("%02x", bytes[i]);
		}
	}

	@Override
	public void formatBinary(InputStream is, OutputStream os) throws IOException {
		throw new UnsupportedOperationException("HexFormatter is a text formatter");
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	public String getContentType() {
		return "text/plain";
	}
}
