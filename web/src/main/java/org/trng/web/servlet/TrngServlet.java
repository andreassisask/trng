package org.trng.web.servlet;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trng.format.RandomFormatter;
import org.trng.service.RequestProcessor;
import org.trng.service.RequestProcessorFactory;
import org.trng.service.ServiceRequest;
import org.trng.service.ServiceResponse;
import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;
import org.trng.service.impl.DefaultRequestProcessorFactory;
import org.trng.web.service.ServletServiceRequest;

@WebServlet(urlPatterns = "/trng", initParams = {
		@WebInitParam(name = TrngServlet.RND_STORE_CLASS_PRM, value = TrngServlet.RND_STORE_CLASS_DEFAULT),
		@WebInitParam(name = TrngServlet.RND_STORE_PROPERTIES_PRM, value = TrngServlet.RND_STORE_PROPERTIES_DEFAULT) })
public class TrngServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(TrngServlet.class);
	private static final long serialVersionUID = 7448697819035028549L;

	public static final String RND_STORE_CLASS_PRM = "randomStoreClass";
	public static final String RND_STORE_CLASS_DEFAULT = "org.trng.store.impl.DummyRandomStore";

	public static final String RND_STORE_PROPERTIES_PRM = "randomStoreProperties";
	public static final String RND_STORE_PROPERTIES_DEFAULT = "";

	private Properties properties;
	private String randomStoreClass;

	private RequestProcessorFactory requestProcessorFactory;

	@Override
	public void init() throws ServletException {
		try {
			doInit(getServletConfig());
		} catch (IOException e) {
			throw new ServletException("Failed to initialize servlet", e);
		}
	}

	protected void doInit(ServletConfig config) throws IOException {
		properties = new Properties();
		requestProcessorFactory = new DefaultRequestProcessorFactory();
		randomStoreClass = config.getInitParameter(RND_STORE_CLASS_PRM);

		String propertiesString = config.getInitParameter(RND_STORE_PROPERTIES_PRM);
		if (propertiesString != null && !propertiesString.isEmpty()) {
			Reader r = new StringReader(propertiesString);
			properties.load(r);
		}
	}

	protected RequestProcessorFactory getRequestProcessorFactory() {
		return requestProcessorFactory;
	}

	protected void setRequestProcessorFactory(RequestProcessorFactory requestProcessorFactory) {
		this.requestProcessorFactory = requestProcessorFactory;
	}

	protected Properties getProperties() {
		return properties;
	}

	protected void setProperties(Properties properties) {
		this.properties = properties;
	}

	protected String getRandomStoreClass() {
		return randomStoreClass;
	}

	protected void setRandomStoreClass(String randomStoreClass) {
		this.randomStoreClass = randomStoreClass;
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		LOG.info("doGet started");
		try {
			ServletServiceRequest serviceRequest = new ServletServiceRequest(httpServletRequest, httpServletResponse);
			RequestProcessor requestProcessor = requestProcessorFactory.getInstance(randomStoreClass, properties);
			processRequest(serviceRequest, httpServletResponse, requestProcessor);
		} catch (Throwable e) {
			LOG.error("doGet failed", e);
			sendErrorQuietly(httpServletResponse, 500, e.getMessage());
		}
		LOG.info("doGet success");
	}

	protected void processRequest(ServiceRequest serviceRequest, HttpServletResponse httpServletResponse,
			RequestProcessor processor) throws InvalidServiceRequestException, ServiceFailedException {

		ServiceResponse serviceResponse = processor.processRequest(serviceRequest);
		RandomFormatter formatter = serviceResponse.getRandomFormatter();
		httpServletResponse.setContentType(formatter.getContentType());

		if (formatter.isBinary()) {
			httpServletResponse.setContentLength(serviceRequest.getQuantity());
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"random.dat\"");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sendErrorQuietly(resp, 500, "Post not supported");
	}

	protected void sendErrorQuietly(HttpServletResponse r, int c, String m) {
		try {
			r.sendError(c, m);
		} catch (IOException e) {
			LOG.error("Failed to send error back to client", e);
		}
	}

}
