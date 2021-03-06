package io.github.oliviercailloux.wutils;

import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

public class Authenticator implements ClientRequestFilter {

	private final String user;
	private final String password;

	public Authenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getBasicAuthentication());
	}

	private String getBasicAuthentication() {
		final Utf8StringAsBase64Sequence encodedUser = Utf8StringAsBase64Sequence.asBase64Sequence(user);
		final Utf8StringAsBase64Sequence encodedPassword = Utf8StringAsBase64Sequence.asBase64Sequence(password);
		/*
		 * “the credentials are constructed by first combining the username and the
		 * password with a colon (aladdin:opensesame), and then by encoding the
		 * resulting string in base64” --
		 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization#
		 * basic_authentication
		 */
		/* TODO provide a collector on AsciiSequence to join. */
		final AsciiSequence userAndPassword = AsciiSequence.fromAsciiSequence(encodedUser + ":" + encodedPassword);
		return "Basic " + Base64Sequence.encode(userAndPassword.getBytes());
	}
}