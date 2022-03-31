package io.github.oliviercailloux.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.quarkus.helpers.AdminClient;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class MissingParameterTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MissingParameterTests.class);

	@TestHTTPResource
	URI serverUri;

	@Inject
	@AdminClient
	Client client;

	@Test
	public void testNoContent() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/put").build();

		try (Response res = client.target(target).request().buildPut(Entity.json("")).invoke()) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		}
	}
}