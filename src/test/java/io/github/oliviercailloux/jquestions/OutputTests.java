package io.github.oliviercailloux.jquestions;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class OutputTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(OutputTests.class);
	@SuppressWarnings("unused")
	private static final org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(OutputTests.class);

	@Test
	void testOut() throws Exception {
		LOGGER.info("Hello, world.");
		LOG.info("Hello, JBoss world.");
	}
}
