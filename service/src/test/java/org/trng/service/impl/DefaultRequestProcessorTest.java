package org.trng.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.trng.format.RandomFormat;
import org.trng.format.RandomFormatter;
import org.trng.format.RandomFormatterFactory;
import org.trng.service.ServiceRequest;
import org.trng.service.exception.InvalidServiceRequestException;
import org.trng.service.exception.ServiceFailedException;
import org.trng.store.RandomStore;

public class DefaultRequestProcessorTest {
	private RandomStore store;
	private ServiceRequest request;
	private DefaultRequestProcessor processor;
	private ByteArrayInputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private byte[] random = new byte[] { 0, 1 };
	private StringWriter writer;
	private RandomFormatterFactory formatterFactory;
	private RandomFormatter formatter;

	@Before
	public void setUp() throws Exception {
		inputStream = new ByteArrayInputStream(random);
		store = Mockito.mock(RandomStore.class);
		request = Mockito.mock(ServiceRequest.class);
		formatterFactory = Mockito.mock(RandomFormatterFactory.class);
		formatter = Mockito.mock(RandomFormatter.class);
		processor = new DefaultRequestProcessor(store, formatterFactory);
		outputStream = new ByteArrayOutputStream();
		writer = new StringWriter();

	}

	@Test
	public void testDefaultRequestProcessor() {
		assertSame(store, processor.getRandomStore());
		assertSame(formatterFactory, processor.getFormatterFactory());
	}

	@Test
	public void testProcessRequestNullRequest() {
		try {
			processor.processRequest(null);
			fail();
		} catch (Throwable e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}

	@Test
	public void testProcessRequestInvalidRequest() throws InvalidServiceRequestException {
		InvalidServiceRequestException e = new InvalidServiceRequestException();
		Mockito.doThrow(e).when(request).validate();

		try {
			processor.processRequest(request);
			fail();
		} catch (Throwable t) {
			assertSame(e, t);
		}
	}

	// @Test
	// public void testProcessRequestQuantityTooBig() throws
	// InvalidServiceRequestException {
	// Mockito.doNothing().when(request).validate();
	// Mockito.doReturn((long) Integer.MAX_VALUE +
	// 1).when(request).getQuantity();
	//
	// try {
	// processor.processRequest(request);
	// fail();
	// } catch (Throwable t) {
	// assertEquals(ServiceFailedException.class, t.getClass());
	// }
	// }

	@Test
	public void testProcessRequestIoException() throws InvalidServiceRequestException, IOException {
		IOException e = new IOException();

		Mockito.doNothing().when(request).validate();
		Mockito.doReturn(16).when(request).getQuantity();
		Mockito.doThrow(e).when(store).getInputStream(Mockito.anyInt());

		try {
			processor.processRequest(request);
			fail();
		} catch (Throwable t) {
			assertEquals(ServiceFailedException.class, t.getClass());
			assertSame(e, t.getCause());
		}
	}

	@Test
	public void testProcessRequestBinary() throws InvalidServiceRequestException, IOException, ServiceFailedException {
		Mockito.doNothing().when(request).validate();
		Mockito.doReturn(random.length).when(request).getQuantity();
		Mockito.doReturn(RandomFormat.RAW).when(request).getFormat();
		Mockito.doReturn(outputStream).when(request).getOutputStream();
		Mockito.doReturn(inputStream).when(store).getInputStream(random.length);
		Mockito.doReturn(true).when(formatter).isBinary();
		Mockito.doReturn(formatter).when(formatterFactory).getFormatter(RandomFormat.RAW);

		Mockito.doNothing().when(formatter)
				.formatBinary(Mockito.any(InputStream.class), Mockito.any(OutputStream.class));

		InOrder io = Mockito.inOrder(request, store, formatterFactory, formatter);
		processor.processRequest(request);
		io.verify(request).validate();
		io.verify(request).getQuantity();
		io.verify(store).getInputStream(random.length);
		io.verify(request).getFormat();
		io.verify(formatterFactory).getFormatter(RandomFormat.RAW);
		io.verify(formatter).isBinary();
		io.verify(request).getOutputStream();
		io.verify(formatter).formatBinary(Mockito.any(InputStream.class), Mockito.any(OutputStream.class));
		io.verifyNoMoreInteractions();
	}

	@Test
	public void testProcessRequestHex() throws InvalidServiceRequestException, IOException, ServiceFailedException {
		Mockito.doNothing().when(request).validate();
		Mockito.doReturn(random.length).when(request).getQuantity();
		Mockito.doReturn(RandomFormat.HEX).when(request).getFormat();
		Mockito.doReturn(writer).when(request).getWriter();
		Mockito.doReturn(inputStream).when(store).getInputStream(random.length);
		Mockito.doReturn(formatter).when(formatterFactory).getFormatter(RandomFormat.HEX);
		Mockito.doReturn(false).when(formatter).isBinary();
		Mockito.doNothing().when(formatter).formatText(Mockito.any(InputStream.class), Mockito.any(Writer.class));

		InOrder io = Mockito.inOrder(request, store, formatterFactory, formatter);
		processor.processRequest(request);
		io.verify(request).validate();
		io.verify(request).getQuantity();
		io.verify(store).getInputStream(random.length);
		io.verify(request).getFormat();
		io.verify(formatterFactory).getFormatter(RandomFormat.HEX);
		io.verify(formatter).isBinary();
		io.verify(request).getWriter();
		io.verify(formatter).formatText(Mockito.any(InputStream.class), Mockito.any(Writer.class));
		io.verifyNoMoreInteractions();
	}

}
