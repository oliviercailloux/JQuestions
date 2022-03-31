package io.github.oliviercailloux.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.quarkus.helpers.AdminClient;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.time.Instant;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This checks that data is persisted after a test even with the
 * {@link TestTransaction} annotation. We can tell because the second request
 * does not add the same user again.
 */
@QuarkusTest
@TestTransaction
@TestMethodOrder(MethodOrderer.MethodName.class)
public class GlobalExternalTransactionTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExternalTransactionTests.class);

	private static Instant testTime;

	private static ImmutableSet<StudentDao> students;

	@BeforeAll
	public static void initTimeStamp() {
		testTime = Instant.now();
		students = ImmutableSet.of(new StudentDao("user test " + testTime.toString(), "p"));
	}

	@TestHTTPResource
	URI serverUri;

	@Inject
	@AdminClient
	Client client;

	@Test
	public void testAddUser_1_initialAdd() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/put").build();

		try (Response res = client.target(target).request().buildPut(Entity.json(students)).invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			assertEquals(1, res.readEntity(Integer.class));
		}
	}

	@Test
	public void testAddUser_2_sameAgain() {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/put").build();

		try (Response res = client.target(target).request().buildPut(Entity.json(students)).invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			assertEquals(0, res.readEntity(Integer.class));
		}
	}
}