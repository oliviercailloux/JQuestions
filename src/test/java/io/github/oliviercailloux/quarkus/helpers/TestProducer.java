package io.github.oliviercailloux.quarkus.helpers;

import io.github.oliviercailloux.wutils.Authenticator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TestProducer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(TestProducer.class);

	@Produces
	@ApplicationScoped
	public Client getClient() {
		return ClientBuilder.newClient();
	}

	@Produces
	@ApplicationScoped
	@AdminClient
	public Client authenticatingClient() {
		return ClientBuilder.newClient().register(Authenticator.fromEnvironment());
	}

	public void close(@Disposes Client client) {
		LOGGER.info("Disposing of JAX-RS client.");
		client.close();
	}
}
