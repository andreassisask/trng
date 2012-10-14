package org.trng.format;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RandomFormat {
	RAW("raw"), ASCII("ascii"), HEX("hex");

	protected static final Map<String, RandomFormat> allFormats = new HashMap<String, RandomFormat>();
	static {
		for (RandomFormat f : EnumSet.allOf(RandomFormat.class)) {
			allFormats.put(f.getName(), f);
		}
	}

	private String name;

	private RandomFormat(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static final RandomFormat get(String name) {
		return allFormats.get(name);
	}

	public static final Collection<RandomFormat> getAllFormats() {
		return allFormats.values();
	}

}
