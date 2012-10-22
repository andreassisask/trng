package org.trng.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.trng.format.RandomFormat;
import org.trng.web.service.FileNameFormatter;

public class SimpleFileNameFormatter implements FileNameFormatter {
	public static final String DATE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
	private static final String FILE_NAME_PATTERN = "%dbytes-%s-%s";
	
	private long time;
	private long quantity;
	private RandomFormat randomFormat;
	
	public SimpleFileNameFormatter(long time, long quantity,
			RandomFormat randomFormat) {
		this.time = time;
		this.quantity = quantity;
		this.randomFormat = randomFormat;
	}

	@Override
	public String getFileName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		String date = dateFormat.format(new Date(time));
		
		return String.format(FILE_NAME_PATTERN, quantity, date, randomFormat);
	}
}
