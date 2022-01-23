package io.github.oliviercailloux.jquestions;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.core.Is;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class ItemsTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemsTests.class);

	@TestHTTPResource
	URI serverUri;

	@Test
	public void testGet() {
		given().when().get("/v0/items").then().statusCode(Response.Status.OK.getStatusCode()).body(Is.is(""));
	}

	@Test
	public void testPost() {
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200).body(MatchesPattern.matchesPattern("MyItem dated .*"));
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200)
				.body(MatchesPattern.matchesPattern("MyItem dated .*\nMyItem dated .*"));
	}

	@Test
	public void testUsingJaxClient() {
		final Client client = ClientBuilder.newClient();
		final URI itemUri = UriBuilder.fromUri(serverUri).path("/v0").path(ItemResource.class).build();
		LOGGER.info("Target: {}.", itemUri);
		try (Response res = client.target(itemUri).request().build("post").invoke()) {
			assertEquals(Response.Status.SEE_OTHER.getStatusCode(), res.getStatus());
		}

		try (Response res = client.target(itemUri).request().get()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
			final String answer = res.readEntity(String.class);
			assertTrue(answer.length() > 10);
		}
	}
}