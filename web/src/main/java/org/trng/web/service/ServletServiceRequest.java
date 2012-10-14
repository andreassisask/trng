package org.trng.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.impl.AbstractServiceRequest;

public class ServletServiceRequest extends AbstractServiceRequest {
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;

	public ServletServiceRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws InvalidServiceRequestException {
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
		this.setParameters(httpServletRequest.getParameterMap());
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public OutputStream getOutputStream() throws IOException {
		return httpServletResponse.getOutputStream();
	}

	public Writer getWriter() throws IOException {
		return httpServletResponse.getWriter();
	}

}
