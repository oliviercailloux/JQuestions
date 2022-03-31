package io.github.oliviercailloux.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AuthenticationTests {

	@TestHTTPResource
	URI serverUri;

	@Test
	public void testAuthenticationNone() {
		final URI target = UriBuilder.fromUri(serverUri).path("/no-such-thing").build();

		try (Response res = ClientBuilder.newClient().target(target).request().buildGet().invoke()) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), res.getStatus());
		}
	}
}