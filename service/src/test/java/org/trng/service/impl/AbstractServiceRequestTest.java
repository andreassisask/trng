package org.trng.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.trng.format.RandomFormat;
import org.trng.service.RequestParameter;
import org.trng.service.RequestParameterParserFactory;
import org.trng.service.exception.InvalidServiceRequestException;

public class AbstractServiceRequestTest {
	private AbstractServiceRequest request;
	private Map<String, String[]> parameters;
	private RequestParameterParserFactory factory;
	private FormatParser formatParser;
	private QuantityParser quantityParser;

	@Before
	public void setUp() throws Exception {
		request = new AbstractServiceRequest() {

			@Override
			public Writer getWriter() throws IOException {
				return null;
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				return null;
			}
		};

		parameters = new HashMap<String, String[]>();

		factory = Mockito.mock(RequestParameterParserFactory.class);
		formatParser = Mockito.mock(FormatParser.class);
		quantityParser = Mockito.mock(QuantityParser.class);

		Mockito.doReturn(formatParser).when(factory).getProcessor(RequestParameter.FORMAT);
		Mockito.doReturn(quantityParser).when(factory).getProcessor(RequestParameter.QUANTITY);
	}

	@Test
	public void testGetFormat() {
		assertNull(request.getFormat());
		request.setFormat(RandomFormat.ASCII);
		assertEquals(RandomFormat.ASCII, request.getFormat());
	}

	@Test
	public void testSetFormat() {
		request.setFormat(RandomFormat.RAW);
		assertEquals(RandomFormat.RAW, request.getFormat());

		request.setFormat(RandomFormat.ASCII);
		assertEquals(RandomFormat.ASCII, request.getFormat());
	}

	@Test
	public void testGetQuantity() {
		assertNull(request.getQuantity());
		request.setQuantity(7);
		assertEquals(new Integer(7), request.getQuantity());
	}

	@Test
	public void testSetQuantity() {
		request.setQuantity(7);
		assertEquals(new Integer(7), request.getQuantity());

		request.setQuantity(17);
		assertEquals(new Integer(17), request.getQuantity());
	}

	@Test
	public void testValidate() throws InvalidServiceRequestException {
		try {
			request.validate();
			fail();
		} catch (Throwable e) {
			assertEquals(InvalidServiceRequestException.class, e.getClass());
		}

		request.setQuantity(8);

		try {
			request.validate();
			fail();
		} catch (Throwable e) {
			assertEquals(InvalidServiceRequestException.class, e.getClass());
		}

		request.setFormat(RandomFormat.ASCII);
		request.validate();
	}

	@Test
	public void testValidateFormat() throws InvalidServiceRequestException {
		try {
			request.validateFormat();
			fail();
		} catch (Throwable e) {
			assertEquals(InvalidServiceRequestException.class, e.getClass());
		}

		request.setFormat(RandomFormat.RAW);
		request.validateFormat();
	}

	@Test
	public void testValidateQuantity() throws InvalidServiceRequestException {
		try {
			request.validateQuantity();
			fail();
		} catch (Throwable e) {
			assertEquals(InvalidServiceRequestException.class, e.getClass());
		}

		request.setQuantity(0);
		try {
			request.validateQuantity();
			fail();
		} catch (Throwable e) {
			assertEquals(InvalidServiceRequestException.class, e.getClass());
		}

		request.setQuantity(1);
		request.validateQuantity();
	}

	@Test
	public void testSetParametersNone() throws InvalidServiceRequestException {
		request.setParameters(parameters, factory);

		parameters.put(RequestParameter.FORMAT.getName(), null);
		request.setParameters(parameters, factory);

		parameters.put(RequestParameter.FORMAT.getName(), new String[] { "a", "b" });
		request.setParameters(parameters, factory);

		parameters.put(RequestParameter.QUANTITY.getName(), null);
		request.setParameters(parameters, factory);

		parameters.put(RequestParameter.QUANTITY.getName(), new String[] { "a", "b" });
		request.setParameters(parameters, factory);

		Mockito.verifyNoMoreInteractions(factory, quantityParser, formatParser);
	}

	@Test
	public void testSetParametersInvalid() throws InvalidServiceRequestException {
		Mockito.doThrow(InvalidServiceRequestException.class).when(quantityParser)
				.processParameterValue(Mockito.anyString());
		Mockito.doThrow(InvalidServiceRequestException.class).when(formatParser)
				.processParameterValue(Mockito.anyString());

		parameters.put(RequestParameter.FORMAT.getName(), new String[] { "x" });
		InOrder io1 = Mockito.inOrder(factory, quantityParser, formatParser);
		try {
			request.setParameters(parameters, factory);
			fail();
		} catch (Throwable t) {
			assertEquals(InvalidServiceRequestException.class, t.getClass());
		}

		io1.verify(factory).getProcessor(RequestParameter.FORMAT);
		io1.verify(formatParser).processParameterValue("x");
		io1.verifyNoMoreInteractions();

		parameters.remove(RequestParameter.FORMAT.getName());
		parameters.put(RequestParameter.QUANTITY.getName(), new String[] { "y" });
		InOrder io2 = Mockito.inOrder(factory, quantityParser, formatParser);
		try {
			request.setParameters(parameters, factory);
			fail();
		} catch (Throwable t) {
			assertEquals(InvalidServiceRequestException.class, t.getClass());
		}

		io2.verify(factory).getProcessor(RequestParameter.FORMAT);
		io2.verify(quantityParser).processParameterValue("y");
		io2.verifyNoMoreInteractions();

	}

	@Test
	public void testSetParametersWithoutFactory() throws InvalidServiceRequestException {
		parameters.put(RequestParameter.FORMAT.getName(), new String[] { RandomFormat.ASCII.getName() });
		parameters.put(RequestParameter.QUANTITY.getName(), new String[] { "10" });
		request.setParameters(parameters);

		assertEquals(RandomFormat.ASCII, request.getFormat());
		assertEquals(new Integer(10), request.getQuantity());
	}

	@Test
	public void testSetParametersValid() throws InvalidServiceRequestException {
		Integer quantity = 12;
		RandomFormat format = RandomFormat.HEX;

		Mockito.doReturn(quantity).when(quantityParser).processParameterValue(Mockito.anyString());
		Mockito.doReturn(format).when(formatParser).processParameterValue(Mockito.anyString());

		parameters.put(RequestParameter.FORMAT.getName(), new String[] { "x" });
		parameters.put(RequestParameter.QUANTITY.getName(), new String[] { "y" });

		InOrder io = Mockito.inOrder(factory, quantityParser, formatParser);
		request.setParameters(parameters, factory);

		assertEquals(quantity, request.getQuantity());
		assertEquals(format, request.getFormat());

		io.verify(factory).getProcessor(RequestParameter.FORMAT);
		io.verify(formatParser).processParameterValue("x");
		io.verify(factory).getProcessor(RequestParameter.QUANTITY);
		io.verify(quantityParser).processParameterValue("y");
		io.verifyNoMoreInteractions();
	}
}
