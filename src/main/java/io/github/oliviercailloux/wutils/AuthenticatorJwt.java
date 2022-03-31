package io.github.oliviercailloux.wutils;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;

public class AuthenticatorJwt implements ClientRequestFilter {

	private final JsonWebStructure jw;
	private final String serialized;

	public AuthenticatorJwt(JsonWebStructure jw) throws JoseException {
		this.jw = checkNotNull(jw);
		serialized = jw.getCompactSerialization();
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getJwtAuthentication());
	}

	private String getJwtAuthentication() {
		return "Bearer " + serialized;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("Structure", jw).toString();
	}

}