package io.github.oliviercailloux.jquestions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.oliviercailloux.wutils.Authenticator;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ExamTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExamTests.class);

	@TestHTTPResource
	URI serverUri;

	@Test
	public void testConnect() {
		final Client client = ClientBuilder.newClient();
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/exam/connect").build();

		try (Response res = client.target(target).request()
				.buildPost(Entity.entity("no ¬ username", MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"))).invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			final String password = res.readEntity(String.class);
			assertTrue(password.length() >= ExamService.PASSWORD_CODE_POINTS);
		}
		try (Response res = client.target(target).request()
				.buildPost(Entity.entity("no ¬ username", MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"))).invoke()) {
			assertEquals(Response.Status.CONFLICT.getStatusCode(), res.getStatus());
		}
		try (Response res = client.target(target).request()
				.buildPost(Entity.entity("another username", MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8")))
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
		}
	}

	@Test
	void testList() throws Exception {
		final Client client = ClientBuilder.newClient();
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/exam/list").build();

		try (Response res = client.target(target).request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), res.getStatus());
		}

		try (Response res = client.target(target).register(new Authenticator("Admin", "wrong password")).request()
				.buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), res.getStatus());
		}

		try (Response res = client.target(target).register(new Authenticator("Admin", "adm")).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
//			final String answer = res.readEntity(String.class);
			final List<Integer> questionIds = res.readEntity(new GenericType<List<Integer>>() {
			});
			assertEquals(1, questionIds.size());
		}
	}
}