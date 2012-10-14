package org.trng.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public interface RandomFormatter {
	public void formatText(InputStream is, Writer w) throws IOException;

	public void formatBinary(InputStream is, OutputStream os) throws IOException;

	/**
	 * Returns if output of the formatter is binary or text.
	 * 
	 * @return True if output is binary, false if text.
	 */
	public boolean isBinary();

	/**
	 * Returns the content type as defined by the mime.
	 * 
	 * @return The mime content type.
	 */
	public String getContentType();

}
