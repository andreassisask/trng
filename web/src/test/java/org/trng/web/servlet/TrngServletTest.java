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
import org.trng.quota.QuotaManager;
import org.trng.quota.QuotaManagerFactory;
import org.trng.service.RequestParameter;
import org.trng.service.RequestProcessor;
import org.trng.service.RequestProcessorFactory;
import org.trng.service.ServiceRequest;
import org.trng.service.ServiceResponse;
import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;
import org.trng.web.service.FileNameFormatter;
import org.trng.web.service.ServletServiceRequest;

public class TrngServletTest {
	public static final String OCTET = "octet";
	public static final String TEXT = "text/plain";
	public static final int QUANTITY = 8;
	public static final String QUANTITY_STR = "8";
	public static final String ASCII = "ascii";

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
	private FileNameFormatter fileNameFormatter;
	private QuotaManagerFactory quotaManagerFactory;
	private QuotaManager quotaManager;

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
		fileNameFormatter = Mockito.mock(FileNameFormatter.class);
		quotaManagerFactory = Mockito.mock(QuotaManagerFactory.class);
		quotaManager = Mockito.mock(QuotaManager.class);

		Mockito.doReturn(true).when(quotaManager).checkAvailable(Mockito.anyString(), Mockito.anyInt());
		Mockito.doReturn(quotaManager).when(quotaManagerFactory).getInstance(Mockito.anyString());
		Mockito.doReturn(requestProcessor).when(requestProcessorFactory)
				.getInstance(Mockito.anyString(), Mockito.any(Properties.class));

		Mockito.doAnswer(new Answer<ServiceResponse>() {
			public ServiceResponse answer(InvocationOnMock invocation) throws Throwable {
				return serviceResponse;
			}
		}).when(requestProcessor).processRequest(Mockito.any(ServiceRequest.class));

		Mockito.doReturn(randomFormatter).when(serviceResponse).getRandomFormatter();
		Mockito.doReturn(parameterMap).when(httpServletRequest).getParameterMap();
		Mockito.doReturn("fileName").when(fileNameFormatter).getFileName();

		parameterMap.put(RequestParameter.QUANTITY.getName(), new String[] { QUANTITY_STR });
		parameterMap.put(RequestParameter.FORMAT.getName(), new String[] { ASCII });

	}

	@Test(expected = ServiceFailedException.class)
	public void testQuotaExceeded() throws ServiceFailedException {
		trngServlet.setQuotaManagerClass("whatever");
		trngServlet.setQuotaManagerFactory(quotaManagerFactory);

		Mockito.doReturn(false).when(quotaManager).checkAvailable(Mockito.anyString(), Mockito.anyInt());
		Mockito.doReturn(QUANTITY).when(serviceRequest).getQuantity();
		Mockito.doReturn("google.com").when(httpServletRequest).getRemoteHost();

		trngServlet.checkQuota(httpServletRequest, serviceRequest);
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

		String remoteHost = "trng.org";
		String randomStoreClass = "a.b.C";
		Properties properties = new Properties();
		String quotaManagerClass = "d.e.F";

		// Check that correct request is passed
		Mockito.doAnswer(new Answer<ServiceResponse>() {
			public ServiceResponse answer(InvocationOnMock invocation) throws Throwable {
				ServletServiceRequest r = (ServletServiceRequest) invocation.getArguments()[0];
				Assert.assertSame(httpServletRequest, r.getHttpServletRequest());
				Assert.assertSame(httpServletResponse, r.getHttpServletResponse());
				return serviceResponse;
			}
		}).when(requestProcessor).processRequest(Mockito.any(ServiceRequest.class));

		Mockito.doReturn(remoteHost).when(httpServletRequest).getRemoteHost();

		trngServlet.setRequestProcessorFactory(requestProcessorFactory);
		trngServlet.setQuotaManagerFactory(quotaManagerFactory);
		trngServlet.setRandomStoreClass(randomStoreClass);
		trngServlet.setQuotaManagerClass(quotaManagerClass);
		trngServlet.setProperties(properties);

		// Just verify that process request is called, the detailed
		// test is already done in testProcessRequestRaw
		InOrder io1 = Mockito.inOrder(requestProcessor, requestProcessorFactory, quotaManagerFactory, quotaManager);
		trngServlet.doGet(httpServletRequest, httpServletResponse);
		io1.verify(quotaManagerFactory).getInstance(quotaManagerClass);
		io1.verify(quotaManager).checkAvailable(remoteHost, QUANTITY);
		io1.verify(requestProcessorFactory).getInstance(randomStoreClass, properties);
		io1.verify(requestProcessor).processRequest(Mockito.any(ServletServiceRequest.class));
		io1.verify(quotaManagerFactory).getInstance(quotaManagerClass);
		io1.verify(quotaManager).removeFromQuota(remoteHost, QUANTITY);
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

		InOrder io = Mockito.inOrder(requestProcessor, serviceResponse, randomFormatter, httpServletResponse, fileNameFormatter);

		trngServlet.processRequest(serviceRequest, httpServletResponse, requestProcessor, fileNameFormatter);

		io.verify(requestProcessor).processRequest(serviceRequest);
		io.verify(serviceResponse).getRandomFormatter();
		io.verify(httpServletResponse).setContentType("bin");
		io.verify(randomFormatter).isBinary();
		io.verify(fileNameFormatter).getFileName();
		io.verify(httpServletResponse).setHeader("Content-Disposition", "attachment; filename=\"fileName\"");
		io.verify(httpServletResponse).setContentLength(8);
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
