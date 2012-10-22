package org.trng.web.service.impl;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.trng.format.RandomFormat;

public class SimpleFileNameFormatterTest {
	private SimpleFileNameFormatter formatter;
	
	private long time;
	private int quantity;
	private RandomFormat randomFormat;
	private String fileName;
	private SimpleDateFormat dateFormat;
	private String dateString;
	
	@Before
	public void setUp() throws Exception {
		time = 1350920760000L;
		quantity = 16;
		randomFormat = RandomFormat.RAW;
		dateFormat = new SimpleDateFormat(SimpleFileNameFormatter.DATE_PATTERN);
		dateString = dateFormat.format(new Date(time));
		fileName = "16bytes-" + dateString + "-RAW";
		formatter = new SimpleFileNameFormatter(time, quantity, randomFormat);
	}

	@Test
	public void testGetFileName() {
		String name = formatter.getFileName();
		assertEquals(fileName, name);
	}

}
