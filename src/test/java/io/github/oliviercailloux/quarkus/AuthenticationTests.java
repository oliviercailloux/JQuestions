package io.github.oliviercailloux.quarkus;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jaris.collections.ImmutableCompleteMap;
import io.github.oliviercailloux.jaris.credentials.CredentialsReader;
import io.github.oliviercailloux.jaris.credentials.CredentialsReader.ClassicalCredentials;
import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.jquestions.dao.UserDao;
import io.github.oliviercailloux.jquestions.entities.User;
import io.github.oliviercailloux.wutils.Authenticator;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.time.Instant;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class AuthenticationTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTests.class);

	static ImmutableCompleteMap<ClassicalCredentials, String> credentials;

	@BeforeAll
	public static void initData() {
		credentials = CredentialsReader.classicalReader().getCredentials();
	}

	@TestHTTPResource
	URI serverUri;

	@Inject
	Client client;

	@Test
	public void testAuthenticationNone() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();

		try (Response res = client.target(target).request().buildGet().invoke()) {
			assertEquals(Response.Status.NO_CONTENT.getStatusCode(), res.getStatus());
		}
	}

	@Test
	public void testAuthenticationBasicUnknown() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();

		checkState(!credentials.get(ClassicalCredentials.API_USERNAME).equals("unknown"));
		checkState(!credentials.get(ClassicalCredentials.API_PASSWORD).equals("wrong password"));

		try (Response res = client.target(target).register(new Authenticator("unknown", "wrong password")).request()
				.buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), res.getStatus());
		}
	}

	@Test
	public void testAuthenticationBasicAdminWrongPassword() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();

		checkState(!credentials.get(ClassicalCredentials.API_PASSWORD).equals("wrong password"));

		try (Response res = client.target(target)
				.register(new Authenticator(credentials.get(ClassicalCredentials.API_USERNAME), "wrong password"))
				.request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), res.getStatus());
		}
	}

	@Test
	public void testAuthenticationBasicAdmin() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();

		try (Response res = client.target(target).register(new Authenticator(credentials)).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			final UserDao content = res.readEntity(UserDao.class);
			final UserDao expected = new UserDao(credentials.get(ClassicalCredentials.API_USERNAME), User.ADMIN_ROLE);
			assertEquals(expected, content);
		}
	}

	@Test
	public void testAuthenticationBasicStudent() {
		final Instant testTime = Instant.now();
		final StudentDao student = new StudentDao("user test " + testTime.toString(), "p");
		{
			final Set<StudentDao> students = ImmutableSet.of(student);

			final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/put").build();

			try (Response res = client.target(target).register(new Authenticator(credentials)).request()
					.buildPut(Entity.json(students)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
				assertEquals(1, res.readEntity(Integer.class));
			}
		}

		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();
		try (Response res = client.target(target).register(new Authenticator(student.username(), student.password()))
				.request().buildGet().invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			final UserDao content = res.readEntity(UserDao.class);
			final UserDao expected = new UserDao(student.username(), User.STUDENT_ROLE);
			assertEquals(expected, content);
		}
	}
}