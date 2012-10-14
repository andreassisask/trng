package org.trng.service;

import org.trng.format.RandomFormatter;

public class ServiceResponse {
	private RandomFormatter randomFormatter;

	public RandomFormatter getRandomFormatter() {
		return randomFormatter;
	}

	public void setRandomFormatter(RandomFormatter randomFormatter) {
		this.randomFormatter = randomFormatter;
	}
}
