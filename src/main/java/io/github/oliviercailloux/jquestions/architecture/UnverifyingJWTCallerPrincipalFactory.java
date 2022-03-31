package io.github.oliviercailloux.jquestions.architecture;

import io.github.oliviercailloux.jquestions.UserService;
import io.github.oliviercailloux.jquestions.UserService.ClaimException;
import io.github.oliviercailloux.jquestions.entities.StudentRegistration;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Improvement of https://quarkus.io/guides/security-jwt#custom-factories.
 *
 * TODO get rid of Quarkus treatment and implement my own principal, a StReg
 * (which could also be a JsonWT if useful later.)
 */
@ApplicationScoped
@Alternative
@Priority(1)
public class UnverifyingJWTCallerPrincipalFactory extends JWTCallerPrincipalFactory {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UnverifyingJWTCallerPrincipalFactory.class);

	@Inject
	UserService userService;

	@Override
	public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
		LOGGER.info("Parsing.");
		try {
			final JsonWebStructure structure = JsonWebStructure.fromCompactSerialization(token);
			structure.setAlgorithmConstraints(AlgorithmConstraints.NO_CONSTRAINTS);
			final String claimsString = structure.getPayload();
//			final String claims = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]),
//					StandardCharsets.UTF_8);
			final JwtClaims claims = JwtClaims.parse(claimsString);
			final StudentRegistration registration = userService.registrationFrom(claims);

			LOGGER.info("Returning claims: {}.", claims);
			return new DefaultJWTCallerPrincipal(claims);
		} catch (JoseException | InvalidJwtException | ClaimException ex) {
			final String message = String.format("Found token %s but no corresponding claim.", token);
			LOGGER.warn(message, ex);
			throw new ParseException(message, ex);
		}
	}
}