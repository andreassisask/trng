package org.trng.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.trng.format.RandomFormat;
import org.trng.service.exception.InvalidServiceRequestException;

public interface ServiceRequest {
	public RandomFormat getFormat();

	public Integer getQuantity();

	public OutputStream getOutputStream() throws IOException;

	public Writer getWriter() throws IOException;

	public void validate() throws InvalidServiceRequestException;

}
