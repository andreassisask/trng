package org.trng.srnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SrndEndpointTest {
	@Mock
	private SrndComponent component;

	@Mock
	private Processor processor;

	private long delay = 50L;
	private long initialDelay = 100L;
	private String provider = "provider";
	private int size = 2;
	private String uri = "uri";
	private String algorithm = "algorithm";

	private SrndEndpoint srndEndpoint;

	@Before
	public void setUp() throws Exception {
		srndEndpoint = new SrndEndpoint(uri, algorithm, component);
		srndEndpoint.setDelay(delay);
		srndEndpoint.setInitialDelay(initialDelay);
		srndEndpoint.setProvider(provider);
		srndEndpoint.setSize(size);
	}

	@Test
	public void testEndpointNoArgs() {
		SrndEndpoint s = new SrndEndpoint();
		assertNull(s.getAlgorithm());
		assertNull(s.getComponent());
		assertTrue(s.getInitialDelay() == 5000);
		assertTrue(s.getDelay() == 5000);
		assertTrue(s.getSize() == 1024);
		assertNull(s.getProvider());
	}

	@Test
	public void testEndpoint() {
		assertSame(component, srndEndpoint.getComponent());
		assertEquals(algorithm, srndEndpoint.getAlgorithm());
		assertEquals(delay, srndEndpoint.getDelay());
		assertEquals(initialDelay, srndEndpoint.getInitialDelay());
		assertEquals(provider, srndEndpoint.getProvider());
		assertEquals(size, srndEndpoint.getSize());
	}

	@Test
	public void testIsSingleton() {
		assertTrue(srndEndpoint.isSingleton());
	}

	@Test
	public void testCreateProducer() throws Exception {
		Producer producer = srndEndpoint.createProducer();
		assertEquals(SrndProducer.class, producer.getClass());
		assertSame(srndEndpoint, producer.getEndpoint());
	}

	@Test
	public void testCreateConsumer() throws Exception {
		Consumer consumer = srndEndpoint.createConsumer(processor);
		assertEquals(SrndConsumer.class, consumer.getClass());
		assertSame(srndEndpoint, consumer.getEndpoint());
	}
}
