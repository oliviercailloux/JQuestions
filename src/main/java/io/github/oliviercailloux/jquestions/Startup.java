package io.github.oliviercailloux.jquestions;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	EntityManager em;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
		LOGGER.info("Loading at startup due to {}.", evt);
	}
}