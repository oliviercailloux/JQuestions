package io.github.oliviercailloux.jquestions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.oliviercailloux.wutils.Authenticator;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class QuestionTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionTests.class);

	@TestHTTPResource
	URI serverUri;

	@Test
	void getQ1AsciiDocTest() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/question/phrasing/1").build();
		final Client client = ClientBuilder.newClient();
		final String response = client.target(target).register(new Authenticator("Admin", "adm"))
				.request("text/asciidoc").buildGet().invoke(String.class);
		assertTrue(response.contains(". 7"));
	}

	@Test
	void getQ1XhtmlTest() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/question/phrasing/1").build();
		final Client client = ClientBuilder.newClient();
		final String response = client.target(target).register(new Authenticator("Admin", "adm"))
				.request(MediaType.APPLICATION_XHTML_XML).buildGet().invoke(String.class);
		LOGGER.info("Received Xhtml: {}.", response);
		assertTrue(response.contains("<!DOCTYPE html>"));
	}

}
