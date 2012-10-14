package org.trng.web.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.trng.format.RandomFormatter;
import org.trng.service.RequestProcessor;
import org.trng.service.RequestProcessorFactory;
import org.trng.service.ServiceRequest;
import org.trng.service.ServiceResponse;
import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;
import org.trng.web.service.ServletServiceRequest;

public class TrngServletTest {
	public static final String OCTET = "octet";
	public static final String TEXT = "text/plain";
	public static final int QUANTITY = 8;
	public static final String QUANTITY_STR = "8";

	private TrngServlet trngServlet;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ServiceRequest serviceRequest;
	private RequestProcessor requestProcessor;
	private ServiceResponse serviceResponse;
	private RandomFormatter randomFormatter;
	private RequestProcessorFactory requestProcessorFactory;
	private Map<String, String[]> parameterMap;
	private ServletConfig servletConfig;

	@Before
	public void setUp() throws Exception {
		trngServlet = new TrngServlet();
		parameterMap = new HashMap<String, String[]>();

		httpServletRequest = Mockito.mock(HttpServletRequest.class);
		httpServletResponse = Mockito.mock(HttpServletResponse.class);
		requestProcessor = Mockito.mock(RequestProcessor.class);
		serviceResponse = Mockito.mock(ServiceResponse.class);
		randomFormatter = Mockito.mock(RandomFormatter.class);
		serviceRequest = Mockito.mock(ServiceRequest.class);
		requestProcessorFactory = Mockito.mock(RequestProcessorFactory.class);
		servletConfig = Mockito.mock(ServletConfig.class);

		Mockito.doReturn(requestProcessor).when(requestProcessorFactory)
				.getInstance(Mockito.anyString(), Mockito.any(Properties.class));

		Mockito.doAnswer(new Answer<ServiceResponse>() {
			public ServiceResponse answer(InvocationOnMock invocation) throws Throwable {
				return serviceResponse;
			}
		}).when(requestProcessor).processRequest(Mockito.any(ServiceRequest.class));

		Mockito.doReturn(randomFormatter).when(serviceResponse).getRandomFormatter();
		Mockito.doReturn(parameterMap).when(httpServletRequest).getParameterMap();

	}

	@Test
	public void testGetRequestProcessorFactory() {
		assertNull(trngServlet.getRequestProcessorFactory());
		trngServlet.setRequestProcessorFactory(requestProcessorFactory);
		assertSame(requestProcessorFactory, trngServlet.getRequestProcessorFactory());
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws ServletException, IOException,
			InvalidServiceRequestException, ServiceFailedException {

		// Check that correct request is passed
		Mockito.doAnswer(new Answer<ServiceResponse>() {
			public ServiceResponse answer(InvocationOnMock invocation) throws Throwable {
				ServletServiceRequest r = (ServletServiceRequest) invocation.getArguments()[0];
				Assert.assertSame(httpServletRequest, r.getHttpServletRequest());
				Assert.assertSame(httpServletResponse, r.getHttpServletResponse());
				return serviceResponse;
			}
		}).when(requestProcessor).processRequest(Mockito.any(ServiceRequest.class));

		String randomStoreClass = "a.b.C";
		Properties properties = new Properties();
		trngServlet.setRequestProcessorFactory(requestProcessorFactory);
		trngServlet.setRandomStoreClass(randomStoreClass);
		trngServlet.setProperties(properties);

		// Just verify that process request is called, the detailed
		// test is already done in testProcessRequest
		InOrder io1 = Mockito.inOrder(requestProcessor, requestProcessorFactory);
		trngServlet.doGet(httpServletRequest, httpServletResponse);
		io1.verify(requestProcessorFactory).getInstance(randomStoreClass, properties);
		io1.verify(requestProcessor).processRequest(Mockito.any(ServletServiceRequest.class));
		io1.verifyNoMoreInteractions();

		// Make sure exception is caught when it happens and sent to client
		InOrder io2 = Mockito.inOrder(httpServletResponse);
		Mockito.doThrow(new ServiceFailedException("m")).when(requestProcessor)
				.processRequest(Mockito.any(ServiceRequest.class));
		trngServlet.doGet(httpServletRequest, httpServletResponse);
		io2.verify(httpServletResponse).sendError(500, "m");
		io2.verifyNoMoreInteractions();

	}

	@Test
	public void testProcessRequestRaw() throws InvalidServiceRequestException, ServiceFailedException {
		Mockito.when(serviceRequest.getQuantity()).thenReturn(8);
		Mockito.when(randomFormatter.getContentType()).thenReturn("bin");
		Mockito.when(randomFormatter.isBinary()).thenReturn(true);

		InOrder io = Mockito.inOrder(requestProcessor, serviceResponse, randomFormatter, httpServletResponse);

		trngServlet.processRequest(serviceRequest, httpServletResponse, requestProcessor);

		io.verify(requestProcessor).processRequest(serviceRequest);
		io.verify(serviceResponse).getRandomFormatter();
		io.verify(httpServletResponse).setContentType("bin");
		io.verify(randomFormatter).isBinary();
		io.verify(httpServletResponse).setContentLength(8);
		io.verify(httpServletResponse).setHeader(Mockito.anyString(), Mockito.anyString());
		io.verifyNoMoreInteractions();
	}

	@Test
	public void testDoPost() throws ServletException, IOException {
		InOrder io = Mockito.inOrder(httpServletRequest, httpServletResponse);

		trngServlet.doPost(httpServletRequest, httpServletResponse);

		io.verify(httpServletResponse).sendError(Mockito.eq(500), Mockito.anyString());
		io.verifyNoMoreInteractions();
	}

	@Test
	public void testSendErrorQuietly() throws IOException {
		trngServlet.sendErrorQuietly(httpServletResponse, 5, "m");
		Mockito.verify(httpServletResponse).sendError(5, "m");
		Mockito.verifyNoMoreInteractions(httpServletResponse);

		// IOException should be caught
		Mockito.doThrow(IOException.class).when(httpServletResponse).sendError(Mockito.anyInt(), Mockito.anyString());
		trngServlet.sendErrorQuietly(httpServletResponse, 5, "m");
	}

	// @Test
	// public void testAddToProperties() {
	// Properties p = new Properties();
	// trngServlet.addToProperties("k", null, p);
	// assertFalse(p.containsKey("k"));
	//
	// trngServlet.addToProperties("k", "", p);
	// assertFalse(p.containsKey("k"));
	//
	// trngServlet.addToProperties("k", "v", p);
	// assertEquals("v", p.get("k"));
	// }

	@Test
	public void testDoInit() throws Exception {
		Mockito.doReturn("a.b.C").when(servletConfig).getInitParameter(TrngServlet.RND_STORE_CLASS_PRM);
		Mockito.doReturn("a=b").when(servletConfig).getInitParameter(TrngServlet.RND_STORE_PROPERTIES_PRM);

		trngServlet.doInit(servletConfig);

		Properties p = trngServlet.getProperties();
		assertNotNull(p);
		assertNotNull(trngServlet.getRequestProcessorFactory());

		assertEquals("a.b.C", trngServlet.getRandomStoreClass());
		assertEquals("b", p.get("a"));

	}
}
