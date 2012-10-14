package org.trng.service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RequestParameter {
	QUANTITY("quantity"), FORMAT("format");

	protected static final Map<String, RequestParameter> allParameters = new HashMap<String, RequestParameter>();
	static {
		for (RequestParameter f : EnumSet.allOf(RequestParameter.class)) {
			allParameters.put(f.getName(), f);
		}
	}

	private String name;

	private RequestParameter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static final RequestParameter get(String name) {
		return allParameters.get(name);
	}

	public static final Collection<RequestParameter> getAll() {
		return allParameters.values();
	}

}
