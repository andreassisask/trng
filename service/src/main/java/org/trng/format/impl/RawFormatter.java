package org.trng.format.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.trng.format.RandomFormatter;

public class RawFormatter implements RandomFormatter {

	@Override
	public void formatText(InputStream is, Writer w) throws IOException {
		throw new UnsupportedOperationException("RawFormatter is a binary formatter");
	}

	@Override
	public void formatBinary(InputStream is, OutputStream os) throws IOException {
		IOUtils.copy(is, os);
	}

	@Override
	public boolean isBinary() {
		return true;
	}

	@Override
	public String getContentType() {
		return "application/octet-stream";
	}

}
