package org.trng.format.impl;

import java.util.HashMap;
import java.util.Map;

import org.trng.format.RandomFormat;
import org.trng.format.RandomFormatter;
import org.trng.format.RandomFormatterFactory;

public class DefaultRandomFormatterFactory implements RandomFormatterFactory {
	private final Map<RandomFormat, RandomFormatter> formatters;

	public DefaultRandomFormatterFactory() {
		formatters = new HashMap<RandomFormat, RandomFormatter>();
		formatters.put(RandomFormat.RAW, new RawFormatter());
		formatters.put(RandomFormat.ASCII, new AsciiFormatter());
		formatters.put(RandomFormat.HEX, new HexFormatter());
	}

	/* (non-Javadoc)
	 * @see org.trng.format.impl.RandomFormatterFactory#getFormatter(org.trng.format.RandomFormat)
	 */
	@Override
	public RandomFormatter getFormatter(RandomFormat format) {
		return formatters.get(format);
	}
}
