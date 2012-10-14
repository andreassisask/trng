/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trng.srnd;

import java.security.SecureRandom;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The srnd consumer.
 */
public class SrndConsumer extends ScheduledPollConsumer {
	private static final Logger LOG = LoggerFactory
			.getLogger(SrndConsumer.class);

	private final SrndEndpoint endpoint;
	private SecureRandom secureRandom;

	public SrndConsumer(SrndEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
		this.endpoint = endpoint;
		this.setInitialDelay(endpoint.getInitialDelay());
		this.setDelay(endpoint.getDelay());
		LOG.info("SrndConsumer created");
	}

	protected SecureRandom getSecureRandom() {
		if (secureRandom == null) {
			secureRandom = createSecureRandom(endpoint);
		}
		return secureRandom;
	}

	private SecureRandom createSecureRandom(SrndEndpoint e) {
		SecureRandom sr = null;

		String provider = e.getProvider();
		String algorithm = e.getAlgorithm();

		try {
			if (provider == null) {
				sr = SecureRandom.getInstance(algorithm);
			} else {
				sr = SecureRandom.getInstance(algorithm, provider);
			}
		} catch (Throwable t) {
			LOG.error("Failed to get SecureRandom instance", t);
		}

		return sr;
	}

	protected byte[] getBytes(SecureRandom sr, int size) {
		LOG.info(String.format("Generating %d bytes of random data", size));
		byte[] b = new byte[size];
		sr.nextBytes(b);
		return b;
	}

	@Override
	protected int poll() throws Exception {
		SecureRandom sr = getSecureRandom();
		if (sr == null)
			return 0;

		byte[] random = getBytes(sr, endpoint.getSize());
		if (random.length == 0)
			return 0;

		Exchange exchange = endpoint.createExchange();
		exchange.getIn().setBody(random);

		try {
			getProcessor().process(exchange);
			return 1;
		} finally {
			// log exception if an exception occurred and was not handled
			if (exchange.getException() != null) {
				getExceptionHandler().handleException(
						"Error processing exchange", exchange,
						exchange.getException());
			}
		}
	}
}
