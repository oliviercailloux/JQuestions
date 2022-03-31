package io.github.oliviercailloux.jquestions;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class Startup {

	@Inject
	EntityManager em;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
	}
}