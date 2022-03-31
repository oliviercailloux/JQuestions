package io.github.oliviercailloux.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.wutils.AuthenticatorJwt;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class AuthenticationJwtTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationJwtTests.class);

	@BeforeAll
	public static void initData() {
		/* */
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
	public void testAuthenticationJwt() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/user/me").build();

		JwtClaims claims = new JwtClaims();
		claims.setIssuer("Issuer"); // who creates the token and signs it
		claims.setAudience("Audience"); // to whom the token is intended to be sent
		claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
		claims.setGeneratedJwtId(); // a unique identifier for the token
		claims.setIssuedAtToNow(); // when the token was issued/created (now)
		claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		claims.setSubject("subject"); // the subject/principal is whom the token is about
		claims.setClaim("email", "mail@example.com"); // additional claims/attributes about the subject can be added
		List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
		claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array
//	    claims.toJson();

		JsonWebSignature jws = new JsonWebSignature();
		jws.setAlgorithmConstraints(AlgorithmConstraints.NO_CONSTRAINTS);
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.NONE);

		try (Response res = client.target(target).register(new AuthenticatorJwt(jws)).request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), res.getStatus());
		}
	}
}