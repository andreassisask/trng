package org.trng.format.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.trng.format.RandomFormatter;

public class AsciiFormatter implements RandomFormatter {
	public static final int BUFFER_SIZE = 1024;

	public void formatText(InputStream is, Writer w) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = is.read(buffer)) >= 0) {
			formatByteArray(buffer, read, w);
		}
	}

	protected void formatByteArray(byte[] bytes, int len, Writer w) throws IOException {
		for (int i = 0; i < len; i++) {
			w.write(StringUtils.leftPad(Integer.toBinaryString(bytes[i]), 8, "0"));
		}
	}

	@Override
	public void formatBinary(InputStream is, OutputStream os) throws IOException {
		throw new UnsupportedOperationException("AsciiFormatter is a text formatter");
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
